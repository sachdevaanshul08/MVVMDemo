package com.demo.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.demo.constant.Constants
import com.demo.repository.network.UserDataSourceFactory

class UserViewModel(private val repository: UserDataSourceFactory) : ViewModel() {
    private val index = MutableLiveData<Int>()
    private val repoResult = map(index) {
        repository.getUserByRange(it, Constants.PAGE_SIZE)
    }
    val usersData = switchMap(repoResult, { it.pagedList })!!
    val networkState = switchMap(repoResult, { it.networkState })!!
    val refreshState = switchMap(repoResult, { it.refreshState })!!

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

}
