package com.demo.di.dashboard

import androidx.lifecycle.ViewModel
import com.demo.di.ViewModelKey
import com.demo.ui.dashboard.DeliveryMainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DeliveryViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DeliveryMainViewModel::class)
    abstract fun bindMyViewModel(deliveryMainViewModel: DeliveryMainViewModel): ViewModel
}