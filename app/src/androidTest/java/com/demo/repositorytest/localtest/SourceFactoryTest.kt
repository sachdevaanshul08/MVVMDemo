package com.demo.repositorytest.localtest

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.TestDeliveryApi
import com.demo.repository.local.DeliveryData
import com.demo.repository.local.dao.DeliveryDao
import com.demo.repository.local.database.DeliveryDatabase
import com.demo.repository.network.paging.AppExecutors
import com.demo.repository.network.paging.DeliveryDataSourceFactory
import com.demo.repository.network.paging.Listing
import com.demo.repository.network.paging.NetworkState
import com.demo.ui.DeliveryMainViewModel
import com.demo.util.EspressoTestingIdlingResource
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class SourceFactoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val deliveryApi = TestDeliveryApi()
    var appExecutors: AppExecutors = AppExecutors(Executors.newSingleThreadExecutor())
    var deliveryDataSourceFactory: DeliveryDataSourceFactory? = null
    var deliveryMainViewModel: DeliveryMainViewModel? = null
    private var deliveryDao: DeliveryDao? = null


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deliveryDatabase = Room.inMemoryDatabaseBuilder(
            context, DeliveryDatabase::class.java
        ).build()
        IdlingRegistry.getInstance().register(EspressoTestingIdlingResource.getIdlingResource())
        deliveryDataSourceFactory = DeliveryDataSourceFactory(
            deliveryDatabase, deliveryDatabase.getDeliveryDao(), deliveryApi, appExecutors
        )
        deliveryMainViewModel = DeliveryMainViewModel(deliveryDataSourceFactory!!)
        deliveryDao = deliveryDatabase.getDeliveryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        IdlingRegistry.getInstance().unregister(EspressoTestingIdlingResource.getIdlingResource())
    }

    /**
     * Check if the data is loaded from network, if not in database
     */
    @Test
    fun fetchDataFromNetwork() {
        val latch = CountDownLatch(2)
        deliveryMainViewModel?.showItemsFrom(0)///deliveryDataSourceFactory?.getUserByRange(0, 10)
        observePagedList(deliveryMainViewModel?.usersData!!, latch)
        latch.await()
        assertThat(deliveryMainViewModel?.usersData?.value?.size, `is`(20))
    }

    /**
     * extract the latest paged list from the listing
     */
    private fun observePagedList(
        listing: Listing<DeliveryData>, latch: CountDownLatch?
    ) {
        val observer = TestObserver<PagedList<DeliveryData>>(latch)
        listing.pagedList.observeForever(observer)
        assertThat(observer.value, `is`(notNullValue()))

    }

    /**
     * extract the latest paged list from the listing
     */
    private fun observePagedList(
        listing: LiveData<PagedList<DeliveryData>>, latch: CountDownLatch?
    ) {
        val observer = TestObserver<PagedList<DeliveryData>>(latch)
        listing.observeForever(observer)
        assertThat(observer.value, `is`(notNullValue()))

    }


    @Test
    fun fetchDataFromLocalDatabase() {

        //Insert the data in database
        deliveryApi.initiateDataModel(10, "Testing")
        val rawData = deliveryApi.getDeliveryDataByRange(0, 10)
        deliveryDao?.insert(rawData)
        //Request the data from datasourcefactory
        val listing = deliveryDataSourceFactory?.getUserByRange(0, 10)
        //trigger the data fetch
        observePagedList(listing!!, null)
        //We don't need latch here, since data is present in the database beforehand
        assertThat(listing.pagedList.value?.size, `is`(10))
    }

    /**
     * asserts the failure message when the load failures
     **/
    @Test
    fun failureHandling() {
        deliveryApi.resetFailureMsg()
        deliveryApi.failureMsg = "Something went wrong"
        val listing = deliveryDataSourceFactory?.getUserByRange(0, 20)
        observePagedList(listing!!, null)
        observeNetworkState(
            listing,
            null
        )  //Without latch test will still pass as there is no background thread in case of failure
        assertThat(listing.networkState.value, `is`(NetworkState.error("Something went wrong")))
    }

    /**
     * extract the latest network state from the listing
     **/
    private fun observeNetworkState(listing: Listing<DeliveryData>, latch: CountDownLatch?) {
        val networkObserver = TestObserver<NetworkState>(latch)
        listing.networkState.observeForever(networkObserver)
    }


    /**
     * simple observer that logs the latest value it receives
     */
    private class TestObserver<T>(latch: CountDownLatch?) : Observer<T> {
        var value: T? = null
        val lat: CountDownLatch? = latch
        override fun onChanged(t: T?) {
            this.value = t
            lat?.countDown()
        }
    }

    /**
     * asserts the retry logic when fetch request fails
     */
    @Test
    fun retryIfFetchFailed() {
        val latch = CountDownLatch(2)
        deliveryApi.failureMsg = "Something went wrong"
        val listing = deliveryDataSourceFactory?.getUserByRange(0, 10)
        observePagedList(listing!!, latch)
        assertThat(listing.pagedList.value?.size, `is`(0)) //Since network failure was there, data size has to be zero

        @Suppress("UNCHECKED_CAST")
        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.networkState.observeForever(networkObserver)
        deliveryApi.resetFailureMsg()
        listing.retry()
        latch.await()
        assertThat(listing.pagedList.value?.size, `is`(10))
        observeNetworkState(listing, null)
        assertThat(listing.networkState.value, `is`(NetworkState.LOADED))
        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.error("Something went wrong"))
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }

    /**
     * asserts refresh that loads the new data
     **/
    @Test
    fun refresh() {
        val latch = CountDownLatch(2)
        val listing = deliveryDataSourceFactory?.getUserByRange(0, 10)
        observePagedList(listing!!, latch)
        latch.await()
        assertThat(listing.pagedList.value?.size, `is`(10))
        @Suppress("UNCHECKED_CAST")
        val refreshObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.refreshState.observeForever(refreshObserver)
        listing.refresh() //As refresh loads 20 items by default
        val newLatch = CountDownLatch(2)
        observePagedList(listing, newLatch)
        newLatch.await()
        observeNetworkState(listing, null)
        assertThat(listing.networkState.value, `is`(NetworkState.LOADED))
        assertThat(listing.pagedList.value?.size, `is`(20))
        val inOrder = Mockito.inOrder(refreshObserver)
        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADING)
        inOrder.verify(refreshObserver).onChanged(NetworkState.LOADED)
    }
}