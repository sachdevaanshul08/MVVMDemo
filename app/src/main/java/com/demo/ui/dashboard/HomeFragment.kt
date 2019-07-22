package com.demo.ui.dashboard

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.demo.R
import com.demo.constant.Constants
import com.demo.databinding.FragmentHomeBinding
import com.demo.repository.local.DeliveryData
import com.demo.repository.network.paging.NetworkState
import com.demo.ui.dashboard.base.BaseFragment
import com.demo.ui.dashboard.common.UserListAdapter
import com.demo.viewmodels.ViewModelFactory
import javax.inject.Inject


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    companion object {
        const val DEFAULT_INDEX = 0
        const val VIEW_STATE = "VIEW_STATE"
        fun newInstance() = HomeFragment()
    }

    @Inject
    lateinit var modelFactory: ViewModelFactory
    private var isLoadedFirstTime: Boolean = false
    private var state: Parcelable? = null

    override val layoutId: Int
        get() = R.layout.fragment_home

    override fun onViewCreation(savedInstanceState: Bundle?) {
        val deliveryMainViewModel = getViewModel()
        initAdapter(deliveryMainViewModel)
        initSwipeToRefresh(deliveryMainViewModel)
        deliveryMainViewModel.showItemsFrom(DEFAULT_INDEX)
        state = savedInstanceState?.getParcelable(VIEW_STATE)
        isLoadedFirstTime = true
    }

    private fun initSwipeToRefresh(deliveryMainViewModel: DeliveryMainViewModel) {
        deliveryMainViewModel.refreshState.observe(this, Observer {
            binding.swipeRefresh.isRefreshing = it == NetworkState.LOADING
        })
        binding.swipeRefresh.setOnRefreshListener {
            deliveryMainViewModel.refresh()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(VIEW_STATE, binding.recyclerView.layoutManager?.onSaveInstanceState())
    }

    private fun getViewModel(): DeliveryMainViewModel {
        return ViewModelProviders.of(this, this.modelFactory).get(DeliveryMainViewModel::class.java)
    }

    private fun onItemClick(deliveryData: DeliveryData?) {
        val fragment = DetailFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable(Constants.DELIVERY_DATA, deliveryData)
        fragment.arguments = bundle
        (activity as MainActivity).openFragment(fragment)
    }

    private fun initAdapter(deliveryMainViewModel: DeliveryMainViewModel) {

        val adapter = UserListAdapter(::onItemClick) {
            deliveryMainViewModel.retry()
        }
        binding.recyclerView.adapter = adapter

        deliveryMainViewModel.usersData.observe(this, Observer<PagedList<DeliveryData>> {
            adapter.submitList(it)
            if (isLoadedFirstTime) {
                binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
                isLoadedFirstTime = false
            }
        })
        deliveryMainViewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

}