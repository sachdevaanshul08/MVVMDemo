package com.demo.ui.dashboard.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.demo.R
import com.demo.util.autoCleared
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment


abstract class BaseFragment<T : ViewDataBinding> : DaggerFragment() {

    private var mRootView: View? = null
    var binding by autoCleared<T>()


    private var parentActivity: AppCompatActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            val activity = context as AppCompatActivity?
            this.parentActivity = activity
        }
    }

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = binding.root
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        //binding.executePendingBindings()
        onViewCreation(savedInstanceState)
    }

    abstract fun onViewCreation(savedInstanceState: Bundle?)

    fun showSnackBar(view: View, msg: String) {
        val snack = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        if (parentActivity != null) {
            snack.view.setBackgroundColor(ContextCompat.getColor(parentActivity!!, R.color.colorAccent))
        }
        snack.show()
    }

}