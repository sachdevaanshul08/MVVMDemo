package com.demo.ui.dashboard

import android.os.Bundle
import com.demo.R
import com.demo.databinding.FragmentDetailsBinding
import com.demo.repository.local.LocationData
import com.demo.repository.local.UserData
import com.demo.ui.dashboard.base.BaseFragment

class DetailFragment : BaseFragment<FragmentDetailsBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_details

    override fun onViewCreation(savedInstanceState: Bundle?) {
        //arguments[]
        val userData = arguments?.getParcelable<UserData>("UserData")
        binding.userdata = userData
    }

    companion object {
        val TAG = DetailFragment::class.java.simpleName
        @JvmStatic
        fun newInstance() = DetailFragment()
    }
}