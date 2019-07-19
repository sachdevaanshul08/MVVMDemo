package com.demo.ui.dashboard

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.demo.R
import com.demo.databinding.FragmentHomeBinding
import com.demo.repository.NetworkState
import com.demo.repository.local.UserData
import com.demo.repository.network.UserDataSourceFactory
import com.demo.ui.dashboard.base.BaseFragment
import com.demo.ui.dashboard.common.UserListAdapter
import javax.inject.Inject


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    companion object {
        const val DEFAULT_INDEX = 0
        const val VIEW_STATE = "VIEW_STATE"
        val TAG = DetailFragment::class.java.simpleName
        @JvmStatic
        fun newInstance() = HomeFragment()
    }


    @Inject
    lateinit var userDataSourceFactory: UserDataSourceFactory
    private lateinit var model: UserViewModel
    private var isLoadedFirstTime: Boolean = false
    private var state: Parcelable? = null

    override val layoutId: Int
        get() = R.layout.fragment_home

    override fun onViewCreation(savedInstanceState: Bundle?) {
        model = getViewModel()
        initAdapter()
        initSwipeToRefresh()
        model.showItemsFrom(DEFAULT_INDEX)
        state = savedInstanceState?.getParcelable(VIEW_STATE)
        isLoadedFirstTime = true
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            binding.swipeRefresh.isRefreshing = it == NetworkState.LOADING
        })
        binding.swipeRefresh.setOnRefreshListener {
            model.refresh()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(VIEW_STATE, binding.recyclerView.layoutManager?.onSaveInstanceState())
    }


    private fun getViewModel(): UserViewModel {
        return UserViewModel(userDataSourceFactory)
    }

    private fun onItemClick(userData: UserData?) {
        val fragment = DetailFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable("UserData", userData)
        fragment.arguments = bundle
        (activity as MainActivity).openFragment(fragment)
    }

    private fun initAdapter() {

        val adapter = UserListAdapter(::onItemClick) {
            model.retry()
        }
        binding.recyclerView.adapter = adapter

        model.usersData.observe(this, Observer<PagedList<UserData>> {
            adapter.submitList(it)
            if (isLoadedFirstTime) {
                binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
                isLoadedFirstTime = false
            }
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

}