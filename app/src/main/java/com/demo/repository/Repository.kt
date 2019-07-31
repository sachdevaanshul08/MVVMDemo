package com.demo.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.demo.repository.datasourcefactory.DataSource
import com.demo.repository.datasourcefactory.Listing
import com.demo.repository.model.DeliveryData
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Acting as single source of truth for the whole application
 */
class Repository @Inject constructor(
    val dataSource: DataSource
) {

    var scope: CoroutineScope? = null

    fun resetCoroutineScope(scope: CoroutineScope?) {
        this.scope = scope
    }

    /**
     * Returns a Listing for the below range.
     */
    fun getDeliveryDataByRange(offset: Int, pageSize: Int): Listing<DeliveryData> {
        // A boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = dataSource.getBoundaryCallBack(pageSize, scope)

        // mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            dataSource.refresh(offset, scope)
        }


        //Fetch the data from local database,
        //If not available then trigger network calls to fetch the data from server
        val livePagedList = dataSource.fetchData(boundaryCallback, pageSize)

        //Listing object is being taken care in viewmodel
        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            dataState = boundaryCallback.dataState,
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}