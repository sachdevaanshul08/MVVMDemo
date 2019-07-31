package com.demo.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.demo.BuildConfig
import com.demo.repository.Repository
import com.demo.repository.datasourcefactory.NetworkState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class HomeViewModel @Inject constructor(val repository: Repository, app: Application) : ViewModel() {
    private val index = MutableLiveData<Int>()
    private val repoResult = map(index) {
        repository.getDeliveryDataByRange(it, BuildConfig.PAGE_SIZE)
    }
    val usersData = switchMap(repoResult) { it.pagedList }
    val networkState = switchMap(repoResult) { it.networkState }
    val refreshState = switchMap(repoResult) { it.refreshState }
    val dataState = switchMap(repoResult) { it.dataState } as MutableLiveData<Boolean>
    var isNetworkErrorDisplayed: Boolean = false
    var isRefreshErrorDisplayed: Boolean = false

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by HomeViewModel.
     * Since we pass viewModelJob, we can cancel all coroutines
     * launched by mainScope by calling viewModelJob.cancel()
     */
    private val mainScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        repository.resetCoroutineScope(mainScope)
    }

    fun refresh(): Boolean {
        if (checkIfAnyStateIsLoading()) return false
        repoResult.value?.refresh?.invoke()
        return true
    }

    fun showItemsFrom(position: Int): Boolean {
        resetDataState()
        if (index.value == position) {
            return false
        }
        index.value = position
        return true
    }

    fun retry(): Boolean {
        if (checkIfAnyStateIsLoading()) return false
        val listing = repoResult.value
        listing?.retry?.invoke()
        return true
    }

    private fun checkIfAnyStateIsLoading(): Boolean {
        return networkState.value == NetworkState.LOADING ||
                refreshState.value == NetworkState.LOADING
    }

    private fun resetDataState() {
        dataState.value = false
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
