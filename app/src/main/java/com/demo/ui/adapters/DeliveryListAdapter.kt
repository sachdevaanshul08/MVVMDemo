package com.demo.ui.adapters

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.data.model.DeliveryData
import com.demo.data.datasourcefactory.NetworkState
import com.demo.ui.adapters.common.NetworkStateViewHolder

/**
 * A PagedList adapter implementation that shows Delivery Items.
 */
class DeliveryListAdapter(
    private val onItemClick: (DeliveryData?) -> Unit,
    private val retryCallback: () -> Unit
) : PagedListAdapter<DeliveryData, RecyclerView.ViewHolder>(POST_COMPARATOR) {
    private var networkState: NetworkState? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.list_item -> (holder as DeliveryDataViewHolder).bind(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateViewHolder).bind(
                networkState
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            (holder as DeliveryDataViewHolder).updateData(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.list_item -> DeliveryDataViewHolder.create(parent, onItemClick)
            R.layout.network_state_item -> NetworkStateViewHolder.create(
                parent,
                retryCallback
            )
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.list_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    /**
     * Add or remove the last item (Loader) in recyclerview
     * based on the network state
     *
     * @param newNetworkState
     */
    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<DeliveryData>() {
            override fun areContentsTheSame(oldItem: DeliveryData, newItem: DeliveryData): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: DeliveryData, newItem: DeliveryData): Boolean =
                oldItem.id == newItem.id
        }
    }
}
