package com.demo.ui.dashboard.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.demo.util.autoCleared
import dagger.android.support.DaggerFragment


abstract class BaseFragment<T : ViewDataBinding> : DaggerFragment() {

    private var mRootView: View? = null
    var binding by autoCleared<T>()

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<T>(inflater, layoutId, container, false)
        mRootView = binding.root
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setLifecycleOwner(this)
        //binding.executePendingBindings()
        onViewCreation(savedInstanceState)
    }

    abstract fun onViewCreation(savedInstanceState: Bundle?)


}