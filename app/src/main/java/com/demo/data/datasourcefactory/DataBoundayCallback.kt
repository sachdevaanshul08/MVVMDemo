package com.demo.data.datasourcefactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.demo.BuildConfig
import com.demo.data.network.apiresponse.ApiEmptyResponse
import com.demo.data.network.apiresponse.ApiErrorResponse
import com.demo.data.network.apiresponse.ApiResponse
import com.demo.data.network.apiresponse.ApiSuccessResponse
import com.demo.util.executors.AppExecutors
import com.demo.util.executors.NewTask
import kotlinx.coroutines.CoroutineScope

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 *
 *
 */
@Suppress("UNCHECKED_CAST")
class DataBoundayCallback<RequestType, ResponseType>(
    val dataSourceRepo: DataAccessProtocol<RequestType, ResponseType>,
    val appExecutors: AppExecutors,
    val networkPageSize: Int,
    val scope: CoroutineScope?
) : PagedList.BoundaryCallback<ResponseType>() {

    val networkState = MutableLiveData<NetworkState>()
    /**
     * Database returned 0 items. query the backend for more items.
     */
    override fun onZeroItemsLoaded() {
        fetchFromNetwork(0, networkPageSize)
    }

    /**
     * User reached to the end of the list.
     */
    override fun onItemAtEndLoaded(itemAtEnd: ResponseType) {
        fetchFromNetwork(dataSourceRepo.getIndexOf(itemAtEnd) + 1, networkPageSize)
    }


    override fun onItemAtFrontLoaded(itemAtFront: ResponseType) {
        // ignored, since we only ever append to what's in the DB
    }

    fun retry(itemAtEnd: ResponseType?) {
        if (itemAtEnd == null) onZeroItemsLoaded() else onItemAtEndLoaded(itemAtEnd)
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
    internal fun refresh(offset: Int): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        val apiResponse = dataSourceRepo.createCall(offset, BuildConfig.PAGE_SIZE)
        apiResponse.onCall().addObserver { p0, p1 ->
            val res = p1 as ApiResponse<RequestType>
            when (res) {
                is ApiSuccessResponse -> {
                    appExecutors.diskIO(object : NewTask {
                        override fun execute() {
                            dataSourceRepo.processResponse(offset, res, true)
                            // since we are in background thread now, post the result.
                            networkState.postValue(NetworkState.LOADED)

                        }
                    }, scope)
                }
                is ApiEmptyResponse -> {
                    dataSourceRepo.onEmptyResponse()
                    // since we are in background thread now, post the result.
                    networkState.postValue(NetworkState.LOADED)

                }
                is ApiErrorResponse -> {
                    dataSourceRepo.onFetchFailed()
                    networkState.postValue(NetworkState.error(res.error.message))
                }
            }
        }
        return networkState
    }

    /**
     * Fetch the data from network
     *
     * @param offset starting index
     * @param limit pagesize
     *
     */
    private fun fetchFromNetwork(
        offset: Int,
        limit: Int
    ) {
        networkState.value = NetworkState.LOADING
        val apiResponse = dataSourceRepo.createCall(offset, limit)
        apiResponse.onCall().addObserver { p0, p1 ->
            val res = p1 as ApiResponse<RequestType>
            when (res) {
                is ApiSuccessResponse -> {
                    appExecutors.diskIO(object : NewTask {
                        override fun execute() {
                            dataSourceRepo.processResponse(offset, res, false)
                            // since we are in background thread now, post the result.
                            networkState.postValue(NetworkState.LOADED)
                        }
                    }, scope)
                }
                is ApiEmptyResponse -> {
                    dataSourceRepo.onEmptyResponse()
                    // since we are in background thread now, post the result.
                    networkState.postValue(NetworkState.LOADED)

                }
                is ApiErrorResponse -> {
                    dataSourceRepo.onFetchFailed()
                    // retrofit calls this on main thread so safe to call set value
                    networkState.postValue(NetworkState.error(res.error.message))
                }
            }
        }
    }
}