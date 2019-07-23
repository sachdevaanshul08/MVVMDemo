package com.demo.ui.dashboard.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.databinding.ListItemBinding
import com.demo.repository.local.DeliveryData

/**
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class UserDataViewHolder(view: View, listItemBinding: ListItemBinding, private val onItemClick: (DeliveryData?) -> Unit) :
    RecyclerView.ViewHolder(view) {
    private var deliveryData: DeliveryData? = null
    var binding: ListItemBinding? = null

    init {
        listItemBinding.root.setOnClickListener {
            //click event on list item
            onItemClick(deliveryData)
        }
        this.binding = listItemBinding
    }

    fun bind(deliveryData: DeliveryData?) {
        this.deliveryData = deliveryData
        binding?.delivery = deliveryData
    }

    companion object {
        fun create(parent: ViewGroup, onItemClick: (DeliveryData?) -> Unit): UserDataViewHolder {
            val binding: ListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item,
                parent,
                false
            )
            return UserDataViewHolder(binding.root, binding, onItemClick)
        }
    }

    fun updateData(item: DeliveryData?) {
        deliveryData = item

    }
}