package com.demo.repository.network

import androidx.annotation.MainThread
import androidx.paging.PagedList
import com.androidx.paging.PagingRequestHelper
import com.demo.repository.local.UserData
import com.demo.repository.network.user.api.UserApi
import com.demo.util.*
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
class UserBoundayCallback(
    private val webservice: UserApi,
    private val handleResponse: (Int, List<UserData>?) -> Unit,
    private val appExecutors: AppExecutors,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<UserData>() {

    val helper = PagingRequestHelper(appExecutors.diskIO())
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
    override fun onItemAtEndLoaded(itemAtEnd: UserData) {
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
        response: List<UserData>?
    ) {
        appExecutors.diskIO().execute {
            handleResponse(offset, response)

        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: UserData) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun fetchFromNetwork(
        offset: Int,
        limit: Int,
        it: PagingRequestHelper.Request.Callback
    ) {
        webservice.getUserData(
            offset,
            limit
        ).enqueue(
            object : Callback<List<UserData>> {
                override fun onFailure(call: Call<List<UserData>>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    it.recordFailure(t)
                }
                override fun onResponse(
                    call: Call<List<UserData>>,
                    response: Response<List<UserData>>
                ) {
                    insertItemsIntoDb(offset, response.body())
                    it.recordSuccess()
                }
            }
        )
    }
}