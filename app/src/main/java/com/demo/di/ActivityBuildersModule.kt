package com.demo.di

import com.demo.di.dashboard.FragmentBuildersModule
import com.demo.ui.dashboard.MainActivity
import com.demo.di.dashboard.MainScope
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun injectMainActivity(): MainActivity


}