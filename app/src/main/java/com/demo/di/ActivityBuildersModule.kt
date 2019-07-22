package com.demo.di

import com.demo.di.dashboard.DeliveryViewModelModule
import com.demo.di.dashboard.FragmentBuildersModule
import com.demo.di.dashboard.MainScope
import com.demo.ui.dashboard.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class, DeliveryViewModelModule::class])
    abstract fun injectMainActivity(): MainActivity
}