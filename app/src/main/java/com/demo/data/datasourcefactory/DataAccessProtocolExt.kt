package com.demo.data.datasourcefactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.demo.util.executors.AppExecutors

/**
 *
 *
 * Acting as single source of truth for the whole application
 *
 *
 * DeliveryRepo implementation that uses a database PagedList + a boundary callback to return a
 * listing that loads in pages.
 *
 *
 *
 *
 */
abstract class DataAccessProtocolExt<RequestType, ResponseType>(
    private val offset: Int,
    private val pageSize: Int,
    private val appExecutors: AppExecutors
) : DataAccessProtocol<RequestType, ResponseType> {

    val dataState = MutableLiveData<Boolean>()

    /**
     * Returns a Listing for the below range.
     */
    private fun getData(offset: Int, pageSize: Int, appExecutors: AppExecutors): Listing<ResponseType> {
        // A boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = DataBoundayCallback(
            dataSourceRepo = this,
            appExecutors = appExecutors,
            networkPageSize = pageSize,
            scope = getViewModelScope()
        )
        // mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            boundaryCallback.refresh(offset)
        }

        //Fetch the data from local database,
        //If not available then trigger network calls to fetch the data from server
        val livePagedList = fetchObservableData(boundaryCallback, pageSize)

        //Listing object is being taken care in viewmodel
        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.retry(it)
            },
            dataState = dataState,
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    /**
     * Fetch the data from local database,
     * If not available then trigger network calls to fetch the data from server
     *
     * it Can be scaled to serve different data tables, if we pass DAO object to fetch kind of data.
     */
    private fun fetchObservableData(
        boundaryCallback: DataBoundayCallback<RequestType, ResponseType>,
        pageSize: Int
    ): LiveData<PagedList<ResponseType>> {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize).setPrefetchDistance(3)
            .setEnablePlaceholders(false)
            .build()
        return LivePagedListBuilder(loadDataSourceFromDb(), config).setBoundaryCallback(boundaryCallback).build()
    }


    fun asListing() = getData(offset, pageSize, appExecutors = appExecutors)

    override fun onEmptyResponse() {
        dataState.postValue(true)
    }

}

