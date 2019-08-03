package com.demo

import android.content.Context
import com.demo.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


class BaseApplication : DaggerApplication() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: BaseApplication
        fun applicationContext() = instance.applicationContext as Context
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

}

