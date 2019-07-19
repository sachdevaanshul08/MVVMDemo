package com.demo.ui.dashboard

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.demo.R
import com.demo.databinding.ActivityMainBinding
import dagger.android.support.DaggerAppCompatActivity


class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        if (savedInstanceState == null) {
            openFragment(HomeFragment.newInstance())
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.frame_container, fragment, "FRAGMENT_TAG")
            .addToBackStack(null).commit()

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}
