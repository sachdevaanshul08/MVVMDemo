package com.demo.di

import com.demo.di.home.HomeViewModelModule
import com.demo.di.home.FragmentBuildersModule
import com.demo.di.home.MainScope
import com.demo.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class, HomeViewModelModule::class])
    abstract fun injectMainActivity(): MainActivity
}