package com.demo.ui

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demo.R
import com.demo.databinding.ActivityMainBinding
import com.demo.ui.base.BaseFragment
import com.demo.ui.home.HomeFragment
import dagger.android.support.DaggerAppCompatActivity


class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        initListeners(binding)

        if (savedInstanceState == null) {
            openFragment(HomeFragment.newInstance())
        }
    }

    private fun initListeners(binding: ActivityMainBinding) =
        binding.toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                onBackPressed()
            }
        })


    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.frame_container, fragment)
            .addToBackStack(null).commit()
    }

    fun isBackButtonRequired(): Boolean {
        return supportFragmentManager.backStackEntryCount > 1
    }

    /**
     *  To show the back button on screen
     */
    fun showBackButton(value: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(value)
    }

    /**
     * This will call @visibleAgain on fragment, which will be visible AGAIN on
     * pressing back button
     *
     */
    private fun triggerFragmentVisible() {
        val fragmentList = supportFragmentManager.fragments
        val listIterator = fragmentList.listIterator(fragmentList.size)
        // Iterate in reverse.
        //To Avoid SupportRequestFragmentManager(Added by glide)
        while (listIterator.hasPrevious()) {
            val fragment = listIterator.previous()
            if (fragment is BaseFragment<*>) {
                fragment.visibleAgain()
                break
            }
        }
    }

    override fun onBackPressed() {
        if (isBackButtonRequired()) {
            supportFragmentManager.popBackStack()
            supportFragmentManager.executePendingTransactions()
            showBackButton(isBackButtonRequired())
            triggerFragmentVisible()
        } else {
            finish()
        }
    }
}
