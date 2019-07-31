package com.demo.di.home

import com.demo.di.map.MapViewModelModule
import com.demo.ui.map.MapDetailFragment
import com.demo.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector(modules = [MapViewModelModule::class])
    abstract fun contributeDetailsFragment(): MapDetailFragment
}