package com.demo.ui.map

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.demo.R
import com.demo.constant.Constants
import com.demo.databinding.FragmentDetailsBinding
import com.demo.repository.model.DeliveryData
import com.demo.ui.base.BaseFragment
import com.demo.viewmodelsfactory.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject


class MapDetailFragment : BaseFragment<FragmentDetailsBinding>() {

    private var mapFragment: SupportMapFragment? = null
    private var deliveryData: DeliveryData? = null
    private var map: GoogleMap? = null
    private var isConnected = false
    private var isMessageDisplayedOnce = false
    private var isLoadedOnce = false

    @Inject
    lateinit var modelFactory: ViewModelFactory

    override val layoutId: Int
        get() = R.layout.fragment_details

    override val title: Int
        get() = R.string.title_Delivery_screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreation(savedInstanceState: Bundle?) {
        //arguments[]
        deliveryData = arguments?.getParcelable(Constants.DELIVERY_DATA)
        binding.deliverydata = deliveryData
        init()
        setUpObserver()
    }

    private fun init() {
        binding.retryButton.setOnClickListener { view ->
            view.visibility = View.GONE
            isMessageDisplayedOnce = false
            doWork(isConnected)
            //setupMap()
        }
    }

    /**
     * Get the desired viewmodel from the factory
     *
     * @return
     */
    private fun getViewModel(): MapViewModel {
        return ViewModelProviders.of(this, this.modelFactory).get(MapViewModel::class.java)
    }

    private fun setUpObserver() {
        val mapViewModel = getViewModel()
        mapViewModel.connectivityObserver.observe(this, Observer { isConnected ->
            isConnected?.let {
                this.isConnected = isConnected
                doWork(isConnected)
            }
        })
    }

    /**
     * Do some work if device network state is changed
     *
     * @param isConnected
     */
    private fun doWork(isConnected: Boolean) {
        if (!isConnected) {
            binding.retryButton.visibility = View.VISIBLE
            binding.retryButton.text =
                resources.getString(if (isLoadedOnce) R.string.offline_map else R.string.reload_map)
            if (!isMessageDisplayedOnce) {
                showSnackBar(binding.root, this.resources.getString(R.string.no_network))
                isMessageDisplayedOnce = true
            }
        } else {
            setupMap()
        }
    }


    override fun onResume() {
        super.onResume()
        setupMap()
        parentActivity.showBackButton(parentActivity.isBackButtonRequired())
    }


    /**
     * Setup the initial map
     *
     */
    private fun setupMap() {
        //Check if mapFragment is not null, than get the retained map fragment and add the market on top of it
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()

            mapFragment?.getMapAsync { googleMap ->
                map = googleMap
                setMapLoadedCallBack(map)
                val latLng: LatLng? = addMarker(googleMap)
                if (latLng != null) googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
            }
            // binding.framemap.id is a FrameLayout, not a Fragment so add the fragment inside it
            childFragmentManager.beginTransaction()
                .replace(binding.frameMap.id, mapFragment as Fragment, MAP_FRAGMENT_TAG)
                .commit()
        } else {
            val tempFragment: SupportMapFragment? =
                childFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment
            tempFragment?.getMapAsync { googleMap ->
                addMarker(googleMap)
                setMapLoadedCallBack(map)
            }
        }

    }

    /**
     * Whether the map has been loaded
     *
     * @param map
     */
    private fun setMapLoadedCallBack(map: GoogleMap?) {
        map?.setOnMapLoadedCallback {
            isLoadedOnce = true
            if (binding.retryButton.isVisible) {
                if (isConnected) {
                    binding.retryButton.visibility = View.GONE
                } else {
                    binding.retryButton.text = resources.getString(R.string.offline_map)
                }
            }
        }
    }

    /**
     * Add the marker on the map
     *
     * @param googleMap map
     * @return latlng
     */
    private fun addMarker(googleMap: GoogleMap): LatLng? {
        var latLng: LatLng? = null
        if (deliveryData?.location != null) {
            latLng = LatLng(deliveryData?.location!!.lat, deliveryData?.location!!.lng)
            googleMap.addMarker(
                MarkerOptions().position(latLng)
                    .title(deliveryData?.location!!.address)
            ).showInfoWindow()
            return latLng
        }
        return latLng
    }

    companion object {
        fun newInstance() = MapDetailFragment()
        const val MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG"
    }
}