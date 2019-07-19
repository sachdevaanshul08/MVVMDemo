package com.demo.repository.network

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.demo.constant.Constants
import com.demo.repository.Listing
import com.demo.repository.NetworkState
import com.demo.repository.local.UserData
import com.demo.repository.local.dao.UserDataDao
import com.demo.repository.local.database.UserDatabase
import com.demo.repository.network.user.api.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository implementation that uses a database PagedList + a boundary callback to return a
 * listing that loads in pages.
 */
class UserDataSourceFactory @Inject constructor(
    val db: UserDatabase,
    val userDataDao: UserDataDao,
    val userApi: UserApi,
    val appExecutors: AppExecutors
) {

    val networkPageSize: Int = Constants.PAGE_SIZE

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertResultIntoDb(index: Int, body: List<UserData>?) {
        body?.let { data ->
            db.runInTransaction {
                val start = index
                val items = data.mapIndexed { index, child ->
                    child.indexInResponse = start + index
                    child
                }
                userDataDao.insert(items)
            }
        }
    }

    /**
     * Run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(offset: Int): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        userApi.getUserData(
            offset,
            networkPageSize
        ).enqueue(
            object : Callback<List<UserData>> {
                override fun onFailure(call: Call<List<UserData>>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(
                    call: Call<List<UserData>>,
                    response: Response<List<UserData>>
                ) {
                    appExecutors.diskIO().execute {
                        db.runInTransaction {
                            userDataDao.delete()
                            insertResultIntoDb(offset, response.body())
                        }
                        // since we are in background thread now, post the result.
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
        )

        return networkState
    }

    /**
     * Returns a Listing for the below range.
     */
    @MainThread
    fun getUserByRange(offset: Int, pageSize: Int): Listing<UserData> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = UserBoundayCallback(
            webservice = userApi,
            handleResponse = this::insertResultIntoDb,
            appExecutors = appExecutors,
            networkPageSize = networkPageSize
        )
        // mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh(offset)
        }

        // Kotlin extension function to convert data to paged list

        /* val livePagedList = userDataDao.getUser().toLiveData(
             pageSize = pageSize,
             boundaryCallback = boundaryCallback
         )*/
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize).setInitialLoadSizeHint(pageSize * 2).setPrefetchDistance(pageSize)
            .setEnablePlaceholders(false)
            .build()
        val livePagedList =
            LivePagedListBuilder(userDataDao.getUser(), config).setBoundaryCallback(boundaryCallback).build()

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}

