package com.demo.util.networkradapter

import java.util.*


abstract class NetworkListener<T> : Observable() {

    abstract fun onCall(): NetworkListener<T>

    fun set(value: T) {
        setChanged()
        notifyObservers(value)
    }

}
