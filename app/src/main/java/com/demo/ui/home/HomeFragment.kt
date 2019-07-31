package com.demo.ui.home

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.demo.R
import com.demo.constant.Constants
import com.demo.databinding.FragmentHomeBinding
import com.demo.repository.datasourcefactory.NetworkState
import com.demo.repository.model.DeliveryData
import com.demo.ui.MainActivity
import com.demo.ui.adapters.DeliveryListAdapter
import com.demo.ui.base.BaseFragment
import com.demo.ui.map.MapDetailFragment
import com.demo.viewmodelsfactory.ViewModelFactory
import javax.inject.Inject


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    @Inject
    lateinit var modelFactory: ViewModelFactory

    private var isLoadedFirstTime: Boolean = false

    private var state: Parcelable? = null

    companion object {
        const val DEFAULT_INDEX = 0
        const val VIEW_STATE = "VIEW_STATE"
        fun newInstance() = HomeFragment()
    }

    override val layoutId: Int
        get() = R.layout.fragment_home


    override val title: Int
        get() = R.string.title_home_screen


    override fun onViewCreation(savedInstanceState: Bundle?) {
        val deliveryMainViewModel = getViewModel()
        initAdapter(deliveryMainViewModel)
        initSwipeToRefresh(deliveryMainViewModel)
        deliveryMainViewModel.showItemsFrom(DEFAULT_INDEX)
        state = savedInstanceState?.getParcelable(VIEW_STATE)
        isLoadedFirstTime = true
    }

    /**
     * Setup swipe_to_refresh view and observer
     *
     * @param homeViewModel ViewModel that is handling the observables
     */
    private fun initSwipeToRefresh(homeViewModel: HomeViewModel) {
        homeViewModel.refreshState.observe(this, Observer {
            this.binding.swipeRefresh.isRefreshing = it == NetworkState.LOADING
            isRefreshOrNetworkSnackBarNeeded(
                homeViewModel,
                it,
                false,
                homeViewModel.isRefreshErrorDisplayed
            )

        })
        this.binding.swipeRefresh.setOnRefreshListener {

            //Start refresh only if other loading is not in progress
            if (!homeViewModel.refresh()) {
                showSnackBar(
                    this.binding.root,
                    resources.getString(R.string.loading_in_progress)
                )
                this.binding.swipeRefresh.isRefreshing = false
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(VIEW_STATE, this.binding.recyclerView.layoutManager?.onSaveInstanceState())
    }

    /**
     * Get the desired viewmodel from the factory
     *
     * @return
     */
    private fun getViewModel(): HomeViewModel {
        return ViewModelProviders.of(this, this.modelFactory).get(HomeViewModel::class.java)
    }

    /**
     * On Item click on the main scree
     *
     * @param deliveryData
     */
    private fun onItemClick(deliveryData: DeliveryData?) {
        val fragment = MapDetailFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable(Constants.DELIVERY_DATA, deliveryData)
        fragment.arguments = bundle
        (activity as MainActivity).openFragment(fragment)
    }

    /**
     * Setup the adapter on the recyerview and
     * also manage the observer to update the changes on the same
     *
     * @param homeViewModel
     */
    private fun initAdapter(homeViewModel: HomeViewModel) {

        val adapter = DeliveryListAdapter(::onItemClick) {
            if (!homeViewModel.retry()) showSnackBar(binding.root, resources.getString(R.string.loading_in_progress))
        }
        binding.recyclerView.adapter = adapter

        homeViewModel.usersData.observe(this, Observer<PagedList<DeliveryData>> {
            adapter.submitList(it)
            if (isLoadedFirstTime) {
                binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
                isLoadedFirstTime = false
            }
        })
        homeViewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
            isRefreshOrNetworkSnackBarNeeded(
                homeViewModel,
                it,
                true,
                homeViewModel.isNetworkErrorDisplayed
            )
        })

        homeViewModel.dataState.observe(this, Observer {
            if (it) {
                showSnackBar(binding.root, resources.getString(R.string.no_data_found))
            }
        })
    }
}