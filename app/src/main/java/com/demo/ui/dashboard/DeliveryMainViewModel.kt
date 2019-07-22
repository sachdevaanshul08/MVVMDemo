package com.demo.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.demo.BuildConfig
import com.demo.repository.network.paging.DeliveryDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class DeliveryMainViewModel @Inject constructor(val repository: DeliveryDataSourceFactory) : ViewModel() {
    private val index = MutableLiveData<Int>()
    private val repoResult = map(index) {
        repository.getUserByRange(it, BuildConfig.PAGE_SIZE)
    }
    val usersData = switchMap(repoResult, { it.pagedList })!!
    val networkState = switchMap(repoResult, { it.networkState })!!
    val refreshState = switchMap(repoResult, { it.refreshState })!!

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by DeliveryMainViewModel.
     * Since we pass viewModelJob, you can cancel all coroutines
     * launched by uiScope by calling viewModelJob.cancel()
     */
    private val mainScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        repository.resetCoroutineScope(mainScope)
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showItemsFrom(position: Int): Boolean {
        if (index.value == position) {
            return false
        }
        index.value = position
        return true
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
