package com.demo.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.demo.R
import com.demo.constant.Constants
import com.demo.databinding.FragmentDetailsBinding
import com.demo.repository.local.DeliveryData
import com.demo.ui.dashboard.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DetailFragment : BaseFragment<FragmentDetailsBinding>() {

    var mapFragment: SupportMapFragment? = null

    override val layoutId: Int
        get() = R.layout.fragment_details

    override fun onViewCreation(savedInstanceState: Bundle?) {
        //arguments[]
        val deliveryData = arguments?.getParcelable<DeliveryData>(Constants.DELIVERY_DATA)
        setupMap(deliveryData)
        binding.deliverydata = deliveryData
    }

    override fun onResume() {
        super.onResume()
        val activitInstance: MainActivity? = activity as MainActivity
        activitInstance?.showBackButton(activitInstance.isBackButtonRequired())

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