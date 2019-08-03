package com.demo.data

import androidx.paging.DataSource
import com.demo.data.datasourcefactory.DataAccessProtocolExt
import com.demo.data.datasourcefactory.Listing
import com.demo.data.db.dao.DeliveryDao
import com.demo.data.db.database.DeliveryDatabase
import com.demo.data.model.DeliveryData
import com.demo.data.network.apiresponse.ApiResponse
import com.demo.data.network.api.DeliveryApi
import com.demo.util.executors.AppExecutors
import com.demo.util.networkradapter.NetworkListener
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Delivery Data DeliveryRepo
 *
 */
class DeliveryRepo @Inject constructor(
    val db: DeliveryDatabase,
    val deliveryDao: DeliveryDao,
    val deliveryApi: DeliveryApi,
    val appExecutors: AppExecutors
) {


    var scope: CoroutineScope? = null

    fun resetCoroutineScope(scope: CoroutineScope?) {
        this.scope = scope
    }

    fun getDeliveryData(offset: Int, limit: Int): Listing<DeliveryData> {
        return object : DataAccessProtocolExt<List<DeliveryData>, DeliveryData>(offset, limit, appExecutors) {

            override fun createCall(offset: Int, pageSize: Int): NetworkListener<ApiResponse<List<DeliveryData>>> {
                return deliveryApi.getDeliveryData(offset, pageSize)
            }

            override fun saveCallResult(offset: Int, items: List<DeliveryData>, isRefreshNeeded: Boolean) {
                if (items.isEmpty()) onEmptyResponse()
                if (isRefreshNeeded) {
                    db.runInTransaction {
                        deliveryDao.delete()
                        saveData(offset, items)
                    }
                } else {
                    saveData(offset, items)
                }

            }

            private fun saveData(index: Int, body: List<DeliveryData>) {
                body.let { data ->
                    db.runInTransaction {
                        val start = index
                        val items = data.mapIndexed { pointer, child ->
                            child.indexInResponse = start + pointer
                            child
                        }
                        deliveryDao.insert(items)
                    }
                }
            }

            override fun getIndexOf(itemAtEnd: DeliveryData?): Int {
                return itemAtEnd?.indexInResponse ?: 0
            }

            override fun loadDataSourceFromDb(): DataSource.Factory<Int, DeliveryData> {
                return deliveryDao.getUser()
            }

            override fun getViewModelScope(): CoroutineScope? {
                return scope
            }

            override fun onFetchFailed() {
                //Do some action here
            }
        }.asListing()
    }


}