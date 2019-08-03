package com.demo.data.network.apiresponse

import com.demo.BaseApplication
import com.demo.R
import retrofit2.Response
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.UnknownHostException


/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
sealed class ApiResponse<T> {

    companion object {

        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(
                Throwable(
                    getMessageToError(
                        t = error
                    )
                )
            )
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(
                        body = body
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    getMessageToCode(response.code())
                }
                ApiErrorResponse(Throwable(errorMsg ?: "unknown error"))
            }
        }

        private fun getMessageToCode(code: Int): String {
            return when (code) {
                HttpURLConnection.HTTP_BAD_REQUEST -> BaseApplication.applicationContext().getString(R.string._400)
                HttpURLConnection.HTTP_INTERNAL_ERROR -> BaseApplication.applicationContext().getString(R.string._500)
                else -> BaseApplication.applicationContext().getString(R.string.something_went_wrong)
                //Multiple codes can be added here
            }
        }

        private fun getMessageToError(t: Throwable): String? {
            val msg = when (true) {
                t is UnknownHostException, t is ConnectException -> BaseApplication.applicationContext().getString(R.string.no_network)
                else -> t.message
            }
            return msg
        }

    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 * can't make it data class as data class requires atleast one parameter
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()


data class ApiErrorResponse<T>(val error: Throwable) : ApiResponse<T>()


