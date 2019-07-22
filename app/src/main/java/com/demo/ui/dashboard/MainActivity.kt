package com.demo.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demo.R
import com.demo.databinding.ActivityMainBinding
import dagger.android.support.DaggerAppCompatActivity


class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        initListeners(binding)

        if (savedInstanceState == null) {
            openFragment(HomeFragment.newInstance())
        }
    }

    private fun initListeners(binding: ActivityMainBinding) {
        binding.toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                onBackPressed()
            }
        })
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.frame_container, fragment, "FRAGMENT_TAG")
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

    override fun onBackPressed() {
        if (isBackButtonRequired()) {
            supportFragmentManager.popBackStack()
            supportFragmentManager.executePendingTransactions()
            showBackButton(isBackButtonRequired())
        } else {
            finish()
        }
    }
}
