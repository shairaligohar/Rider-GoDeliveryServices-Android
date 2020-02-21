package com.godeliveryservices.rider.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.dummy.DummyContent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment(), OnMapReadyCallback, OnListFragmentInteractionListener {
    private lateinit var mMap: GoogleMap
    private lateinit var homeViewModel: HomeViewModel
    // TODO: Customize parameters
    private var columnCount = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        with(root.new_orders_list) {
            layoutManager = when {
                columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(context)
                else -> androidx.recyclerview.widget.GridLayoutManager(context, columnCount)
            }
            adapter =
                NewOrderRecyclerViewAdapter(
                    listOf(DummyContent.DummyItem("", "", "")),
                    this@HomeFragment
                )
        }
        with(root.active_orders_list) {
            layoutManager = when {
                columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(context)
                else -> androidx.recyclerview.widget.GridLayoutManager(context, columnCount)
            }
            adapter =
                ActiveOrdersRecyclerViewAdapter(
                    listOf(DummyContent.DummyItem("", "", ""), DummyContent.DummyItem("", "", "")),
                    this@HomeFragment
                )
        }
        return root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        mMap.isMyLocationEnabled = true

        mMap.setOnMapLoadedCallback {

            // Add a marker in Sydney and move the camera
            val sydney = LatLng(-34.0, 151.0)
            mMap.addMarker(
                MarkerOptions().position(sydney).icon(
                    BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_maps_marker_32dp
                    )
                ).title("You're here!")
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {

    }
}