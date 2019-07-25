package com.demo.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
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

    @get:StringRes
    abstract val title: Int


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = binding.root
        return mRootView
    }

    /**
     * This function triggers when fragment is visible again after returning from some other fragment
     */
    fun visibleAgain() {
        setTitle(parentActivity?.resources?.getString(title))
    }

    override fun onResume() {
        super.onResume()
        setTitle(parentActivity?.resources?.getString(title))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        //binding.executePendingBindings()
        onViewCreation(savedInstanceState)
    }

    /**
     * set the title of fragment
     */
    private fun setTitle(title: String?) {
        parentActivity?.supportActionBar?.setTitle(title)
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