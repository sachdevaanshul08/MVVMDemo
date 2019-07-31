package com.demo.di.home

import androidx.lifecycle.ViewModel
import com.demo.di.ViewModelKey
import com.demo.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindMyViewModel(homeViewModel: HomeViewModel): ViewModel
}