package com.demo.repository.network.paging

import androidx.annotation.MainThread
import androidx.paging.PagedList
import com.androidx.paging.PagingRequestHelper
import com.demo.repository.local.DeliveryData
import com.demo.repository.network.user.api.DeliveryApi
import com.demo.util.createStatusLiveData
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class DataBoundayCallback(
    private val webservice: DeliveryApi,
    private val handleResponse: (Int, List<DeliveryData>?) -> Unit,
    private val appExecutors: AppExecutors,
    private val networkPageSize: Int,
    private val scope: CoroutineScope?
) : PagedList.BoundaryCallback<DeliveryData>() {

    val helper = PagingRequestHelper(appExecutors.diskIOExecutor())
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned 0 items. query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            fetchFromNetwork(0, networkPageSize, it)
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: DeliveryData) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            fetchFromNetwork(itemAtEnd.indexInResponse + 1, networkPageSize, it)
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
        offset: Int,
        response: List<DeliveryData>?, it: PagingRequestHelper.Request.Callback
    ) {
        appExecutors.diskIO(object : NewTask {
            override fun execute() {
                handleResponse(offset, response)
                it.recordSuccess()
            }
        }, scope)
    }

    override fun onItemAtFrontLoaded(itemAtFront: DeliveryData) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun fetchFromNetwork(
        offset: Int,
        limit: Int,
        it: PagingRequestHelper.Request.Callback
    ) {
        webservice.getDeliveryData(
            offset,
            limit
        ).enqueue(
            object : Callback<List<DeliveryData>> {
                override fun onFailure(call: Call<List<DeliveryData>>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    it.recordFailure(t)
                }

                override fun onResponse(
                    call: Call<List<DeliveryData>>,
                    response: Response<List<DeliveryData>>
                ) {
                    if (response.code() == 200) {
                        insertItemsIntoDb(offset, response.body(), it)
                    } else {
                        onFailure(call, Throwable(response.message()))
                    }
                }
            }
        )
    }
}