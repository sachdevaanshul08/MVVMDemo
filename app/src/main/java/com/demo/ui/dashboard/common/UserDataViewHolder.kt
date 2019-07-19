package com.demo.ui.dashboard.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.R
import com.demo.databinding.ListItemBinding
import com.demo.repository.local.UserData

/**
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class UserDataViewHolder(view: View, listItemBinding: ListItemBinding, private val onItemClick: (UserData?) -> Unit) :
    RecyclerView.ViewHolder(view) {
    private var userData: UserData? = null
    var binding: ListItemBinding? = null

    init {
        listItemBinding.root.setOnClickListener {
            //click event on list item
            onItemClick(userData)
        }
        this.binding = listItemBinding
    }

    fun bind(userData: UserData?) {
        this.userData = userData
        binding?.userdata = userData
    }

    companion object {
        fun create(parent: ViewGroup, onItemClick: (UserData?) -> Unit): UserDataViewHolder {
            val binding: ListItemBinding = DataBindingUtil.inflate<ListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item,
                parent,
                false
            )
            return UserDataViewHolder(binding.root, binding, onItemClick)
        }
    }

    fun updateData(item: UserData?) {
        userData = item

    }
}