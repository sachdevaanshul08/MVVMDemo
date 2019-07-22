package com.demo.ui.dashboard.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.util.Status
import com.demo.databinding.NetworkStateItemBinding
import com.demo.repository.network.paging.NetworkState


/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateViewHolder(
    view: View,
    networkStateItemBinding: NetworkStateItemBinding,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(view) {
    var binding: NetworkStateItemBinding? = null

    init {
        networkStateItemBinding.retryButton.setOnClickListener {
            retryCallback()
        }
        this.binding = networkStateItemBinding;
    }

    fun bind(networkState: NetworkState?) {
        binding?.progressBar?.visibility = toVisibility(networkState?.status == Status.RUNNING)
        binding?.retryButton?.visibility = toVisibility(networkState?.status == Status.FAILED)
        binding?.errorMsg?.visibility = toVisibility(networkState?.msg != null)
        binding?.errorMsg?.text = networkState?.msg
    }//binding?.networkstate=networkState }

    fun toVisibility(constraint: Boolean): Int {
        return if (constraint) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val binding: NetworkStateItemBinding = DataBindingUtil.inflate<NetworkStateItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.network_state_item,
                parent,
                false
            )
            return NetworkStateViewHolder(binding.root, binding, retryCallback)
        }
    }
}
