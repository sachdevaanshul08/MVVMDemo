package com.demo.data.datasourcefactory

import androidx.paging.DataSource
import com.demo.data.network.apiresponse.ApiResponse
import com.demo.data.network.apiresponse.ApiSuccessResponse
import com.demo.util.networkradapter.NetworkListener
import kotlinx.coroutines.CoroutineScope

/**
 * Basic function that any use case handler need to implement in order
 * to communicate with the data source factory
 *
 * @param RequestType
 * @param ResponseType
 */
interface DataAccessProtocol<RequestType, ResponseType> {

    //Return the api call
    fun createCall(offset: Int, pageSize: Int): NetworkListener<ApiResponse<RequestType>>

    //save the returned response from the server
    fun saveCallResult(offset: Int, items: RequestType, isRefreshNeeded: Boolean)

    //Load the data from local db
    fun loadDataSourceFromDb(): DataSource.Factory<Int, ResponseType>

    //Get the viewmodel scope
    fun getViewModelScope(): CoroutineScope?

    //Get the index of @itemAtEnd object
    fun getIndexOf(itemAtEnd: ResponseType?): Int

    //For data layer internal use only
    fun processResponse(
        offset: Int,
        response: ApiSuccessResponse<RequestType>,
        isRefreshNeeded: Boolean
    ) {
        //do something here
        saveCallResult(offset, response.body, isRefreshNeeded)

    }

    //In case if the api failed
    fun onFetchFailed()

    //In case if the response is empty
    fun onEmptyResponse()
}