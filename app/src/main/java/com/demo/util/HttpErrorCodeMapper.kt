package com.demo.util

import android.app.Application
import com.demo.R
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpErrorCodeMapper @Inject constructor(val application: Application) {

    /**
     * Map the code to its pre defined message
     *
     * @param code
     * @return
     */
    fun getMessageToCode(code: Int): String {

        val httpCode = when (code) {
            401 -> HTTPCode._401
            500 -> HTTPCode._500
            //Can be scaled here
            else -> HTTPCode._DEFAULT
        }
        return application.getString(httpCode.msg)
    }

    /**
     * maps the throwable to user understandable message
     *
     * @param t
     * @return
     */
    fun getMessageToError(t: Throwable): String? {

        val msg = when (true) {
            t is UnknownHostException -> application.resources.getString(R.string.no_network)
            else -> t.message
        }
        return msg
    }

}

private enum class HTTPCode(val msg: Int) {
    _401(R.string._401),
    _500(R.string._500),
    _DEFAULT(R.string.something_went_wrong)
    //Multiple codes can be added here
}



