package com.demo.di

import com.demo.di.home.DeliveryViewModelModule
import com.demo.di.home.FragmentBuildersModule
import com.demo.di.home.MainScope
import com.demo.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class, DeliveryViewModelModule::class])
    abstract fun injectMainActivity(): MainActivity
}