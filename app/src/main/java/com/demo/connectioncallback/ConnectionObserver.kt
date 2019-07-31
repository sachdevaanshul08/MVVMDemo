package com.demo.connectioncallback

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

/**
 * This class facilitates the connection state changed observer for the application
 * ie. if the device is offline or online
 *
 * @property context  context
 */
class ConnectionObserver(val context: Context) : LiveData<Boolean>() {

    private var connectivityManager: ConnectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var connectivityCallBack: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> connectivityManager.registerDefaultNetworkCallback(
                getConnectivityCallBack()
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> registerForLollipop()
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityCallBack)

    }

    private fun registerForLollipop() {
        val builder = NetworkRequest.Builder()
            .addTransportType(TRANSPORT_CELLULAR)
            .addTransportType(TRANSPORT_WIFI)
        connectivityManager.registerNetworkCallback(builder.build(), getConnectivityCallBack())
    }

    private fun getConnectivityCallBack(): ConnectivityManager.NetworkCallback {

        connectivityCallBack = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) {
                postValue(true)
            }

            override fun onLost(network: Network?) {
                postValue(false)
            }
        }
        return connectivityCallBack
    }

    private fun updateConnection() {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}