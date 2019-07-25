package com.demo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.demo.R
import com.demo.constant.Constants
import com.demo.databinding.FragmentDetailsBinding
import com.demo.repository.local.DeliveryData
import com.demo.ui.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DetailFragment : BaseFragment<FragmentDetailsBinding>() {

    var mapFragment: SupportMapFragment? = null

    override val layoutId: Int
        get() = R.layout.fragment_details

    override val title: Int
        get() = R.string.title_Delivery_screen

    override fun onViewCreation(savedInstanceState: Bundle?) {
        //arguments[]
        val deliveryData = arguments?.getParcelable<DeliveryData>(Constants.DELIVERY_DATA)
        setupMap(deliveryData)
        binding.deliverydata = deliveryData
    }


    override fun onResume() {
        super.onResume()
        val activityInstance: MainActivity? = activity as MainActivity
        activityInstance?.showBackButton(activityInstance.isBackButtonRequired())
    }

    private fun setupMap(deliveryData: DeliveryData?) {
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
        }
        mapFragment?.getMapAsync(OnMapReadyCallback { googleMap ->
            val latLng = LatLng(deliveryData!!.location.lat, deliveryData.location.lng)
            googleMap.addMarker(
                MarkerOptions().position(latLng)
                    .title(deliveryData.location.address)
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
        })

        // binding.framemap.id is a FrameLayout, not a Fragment
        childFragmentManager.beginTransaction().replace(binding.frameMap.id, mapFragment as Fragment).commit()
    }

    companion object {
        fun newInstance() = DetailFragment()
    }

}