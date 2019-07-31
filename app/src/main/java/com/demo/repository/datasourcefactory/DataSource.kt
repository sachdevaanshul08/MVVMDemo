package com.demo.repository.datasourcefactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.androidx.paging.PagingRequestHelper
import com.demo.BuildConfig
import com.demo.repository.model.DeliveryData
import com.demo.repository.db.dao.DeliveryDao
import com.demo.repository.db.database.DeliveryDatabase
import com.demo.util.executors.AppExecutors
import com.demo.util.executors.NewTask
import com.demo.repository.network.api.DeliveryApi
import com.demo.util.HttpErrorCodeMapper
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 *
 * Repository implementation that uses a database PagedList + a boundary callback to return a
 * listing that loads in pages.
 *
 *
 * /Dao and API in the constructor below can be provided manually in the data source from the repository based on
 * the data request to scale the application to serve different tables and apis
 *
 */
open class DataSource @Inject constructor(
    val db: DeliveryDatabase,
    val deliveryDao: DeliveryDao,
    val deliveryApi: DeliveryApi,
    val appExecutors: AppExecutors,
    val httpErrorCodeMapper: HttpErrorCodeMapper
) {

    /**
     * Fetch the data from local database,
     * If not available then trigger network calls to fetch the data from server
     *
     * it Can be scaled to serve different data tables, if we pass DAO object to fetch kind of data.
     */
    internal fun fetchData(boundaryCallback: DataBoundayCallback, pageSize: Int):
            LiveData<PagedList<DeliveryData>> {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize).setPrefetchDistance(pageSize / 5)
            .setEnablePlaceholders(false)
            .build()
        return LivePagedListBuilder(deliveryDao.getUser(), config).setBoundaryCallback(boundaryCallback).build()
    }

    /**
     *  create a boundary callback which will observe when the user reaches to the edges of
     *  the list and update the database with extra data.
     */
    internal fun getBoundaryCallBack(pageSize: Int, scope: CoroutineScope?): DataBoundayCallback {
        return DataBoundayCallback(
            webservice = deliveryApi,
            handleResponse = this::insertResultIntoDb,
            appExecutors = appExecutors,
            networkPageSize = pageSize,
            scope = scope,
            httpErrorCodeMapper = httpErrorCodeMapper
        )
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    internal fun insertResultIntoDb(index: Int, body: List<DeliveryData>?) {
        body?.let { data ->
            db.runInTransaction {
                val start = index
                val items = data.mapIndexed { index, child ->
                    child.indexInResponse = start + index
                    child
                }
                deliveryDao.insert(items)
            }
        }
    }

    /**
     * Run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     *
     * it can be scaled to serve multiple apis, based of the api type
     */
    internal fun refresh(offset: Int, scope: CoroutineScope?): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        deliveryApi.getDeliveryData(
            offset,
            BuildConfig.PAGE_SIZE
        ).enqueue(
            object : Callback<List<DeliveryData>> {
                override fun onFailure(call: Call<List<DeliveryData>>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    networkState.value = NetworkState.error(httpErrorCodeMapper.getMessageToError(t))
                }

                override fun onResponse(
                    call: Call<List<DeliveryData>>,
                    response: Response<List<DeliveryData>>
                ) {
                    if (response.isSuccessful) {
                        appExecutors.diskIO(object : NewTask {
                            override fun execute() {

                                db.runInTransaction {
                                    deliveryDao.delete()
                                    insertResultIntoDb(offset, response.body())
                                }
                                // since we are in background thread now, post the result.
                                networkState.postValue(NetworkState.LOADED)

                            }
                        }, scope)
                    } else {
                        onFailure(call, Throwable(httpErrorCodeMapper.getMessageToCode(response.code())))
                    }
                }
            }
        )
        return networkState
    }

}

