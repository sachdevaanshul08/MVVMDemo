package com.demo.di.dashboard

import com.demo.ui.dashboard.DetailFragment
import com.demo.ui.dashboard.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule{

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment():HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailsFragment():DetailFragment
}