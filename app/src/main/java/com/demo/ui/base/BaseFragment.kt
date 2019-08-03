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
import com.demo.data.datasourcefactory.NetworkState
import com.demo.ui.MainActivity
import com.demo.ui.home.HomeViewModel
import com.demo.util.Status
import com.demo.util.autoCleared
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment


abstract class BaseFragment<T : ViewDataBinding> : DaggerFragment() {

    private var mRootView: View? = null
    var binding by autoCleared<T>()

    lateinit var parentActivity: MainActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            val activity = context as MainActivity
            this.parentActivity = activity
        }
    }

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * @return title
     */
    @get:StringRes
    abstract val title: Int


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = binding.root
        return mRootView
    }

    /**
     * This function triggers when fragment is visible
     * again after returning from some other fragment
     */
    fun visibleAgain() {
        setTitle(parentActivity.resources?.getString(title))
    }

    override fun onResume() {
        super.onResume()
        setTitle(parentActivity.resources?.getString(title))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        //binding.executePendingBindings()
        onViewCreation(savedInstanceState)
    }

    /**
     * Set the title on the toolbar
     *
     * @param title to be set
     */
    private fun setTitle(title: String?) {
        parentActivity.supportActionBar?.setTitle(title)
    }

    abstract fun onViewCreation(savedInstanceState: Bundle?)

    /**
     * Show the snackbar
     *
     * @param view base view
     * @param msg message to be displayed
     */
    fun showSnackBar(view: View, msg: String?) {
        if (msg.isNullOrBlank()) return
        val snack = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        snack.view.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorAccent))
        snack.show()
    }

    /**
     * This function notify the user in the form of snackbar, if there has been changes
     * in the network state while doing paging or while pull to refresh
     *
     * @param viewModel viewmodel which provides live data to observe on i.e. @HomeViewModel
     * @param networkState network state
     * @param isNetworkOrRefreshState true -> if its paging network state else refresh network state that we are observing
     * @param actualState actual current state of the network (paging or refresh)
     */
    protected fun isRefreshOrNetworkSnackBarNeeded(
        viewModel: HomeViewModel,
        networkState: NetworkState,
        isNetworkOrRefreshState: Boolean, actualState: Boolean
    ) {
        var tempBool: Boolean = actualState

        if (networkState.status == Status.FAILED) {
            if (!tempBool) {
                showSnackBar(binding.root, networkState.msg)
                tempBool = true
            }
        } else {
            tempBool = false
        }

        if (isNetworkOrRefreshState) {
            viewModel.isNetworkErrorDisplayed = tempBool
        } else {
            viewModel.isRefreshErrorDisplayed = tempBool
        }
    }
}