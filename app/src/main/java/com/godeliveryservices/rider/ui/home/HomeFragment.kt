package com.godeliveryservices.rider.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.repository.PreferenceRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), OnMapReadyCallback, OnListFragmentInteractionListener {
    private lateinit var mMap: GoogleMap
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var newOrdersAdapter: NewOrderRecyclerViewAdapter
    private lateinit var activeOrdersAdapter: ActiveOrdersRecyclerViewAdapter

    private val riderId by lazy { PreferenceRepository(requireContext()).getRiderId() }
    private val riderStatus by lazy { PreferenceRepository(requireContext()).getRiderStatus() }

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
            newOrdersAdapter = NewOrderRecyclerViewAdapter(
                listOf(),
                this@HomeFragment
            )
            layoutManager = when {
                columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(context)
                else -> androidx.recyclerview.widget.GridLayoutManager(context, columnCount)
            }
            adapter = newOrdersAdapter
        }
        with(root.active_orders_list) {
            activeOrdersAdapter =
                ActiveOrdersRecyclerViewAdapter(
                    listOf(),
                    this@HomeFragment
                )
            layoutManager = when {
                columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(context)
                else -> androidx.recyclerview.widget.GridLayoutManager(context, columnCount)
            }
            adapter = activeOrdersAdapter
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
        //mMap.isMyLocationEnabled = true

        mMap.setOnMapLoadedCallback {

            // Add a marker in Sydney and move the camera
//            val sydney = LatLng(-34.0, 151.0)
//            mMap.addMarker(
//                MarkerOptions().position(sydney).icon(
//                    BitmapDescriptorFactory.fromResource(
//                        R.drawable.ic_maps_marker_32dp
//                    )
//                ).title("You're here!")
//            )
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        context?.registerReceiver(mMessageReceiver, IntentFilter("Refresh"))
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(mMessageReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onStatusClickListener(item: Order) {
        if (item.RiderID == null) {
            item.RiderID = PreferenceRepository(requireContext()).getRiderId()
        }
        homeViewModel.updateOrderStatus(item)
    }

    override fun onNavigationClickListener(item: Order) {
        val address =
            if (item.Status == "Accepted") item.BranchAddress else item.CustomerAddress
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("google.navigation:q=${address}")
        )
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
        startActivity(intent)
    }

    override fun onBranchContactClickListener(item: Order) {
        dialIntent(item.BranchContact)
    }

    override fun onCustomerContactClickListener(item: Order) {
        dialIntent(item.CustomerNumber)
    }

    private fun dialIntent(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${number}")
        ContextCompat.startActivity(requireContext(), intent, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupData()
    }

    private fun setupData() {
        status_toggle.isChecked = riderStatus.equals("Active")
        status_toggle.setOnClickListener { _ ->
            homeViewModel.updateStatus(
                riderId = riderId,
                isActive = status_toggle.isChecked
            )
        }
        instantiateFirebaseToken(riderId)
        homeViewModel.fetchOrders(riderId)
    }

    private fun setupObservers() {
        homeViewModel.riderPendingOrders.observe(viewLifecycleOwner, Observer { orders ->
            newOrdersAdapter.setValues(orders)
        })

        homeViewModel.riderActiveOrders.observe(viewLifecycleOwner, Observer { orders ->
            activeOrdersAdapter.setValues(orders)
        })

        homeViewModel.showLoading.observe(viewLifecycleOwner, Observer { flag ->
            loading.visibility = if (flag) View.VISIBLE else View.GONE
            status_toggle.isEnabled = flag.not()
        })

        homeViewModel.responseMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        })

        homeViewModel.riderStatusUpdated.observe(viewLifecycleOwner, Observer { yes ->
            if (yes) {
                PreferenceRepository(requireContext())
                    .updateRiderStatus(if (status_toggle.isChecked) "Active" else "Inactive")
                if (status_toggle.isChecked)
                    homeViewModel.fetchOrders(riderId)
            } else {
                status_toggle.isChecked = true
            }
        })

        homeViewModel.noActiveOrder.observe(viewLifecycleOwner, Observer { yes ->
            no_active_orders_text.visibility = if (yes) View.VISIBLE else View.GONE
        })

        homeViewModel.noNewOrder.observe(viewLifecycleOwner, Observer { yes ->
            no_new_orders_text.visibility = if (yes) View.VISIBLE else View.GONE
        })
    }

    private fun instantiateFirebaseToken(riderId: Long) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Firebase Token", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                token?.let { homeViewModel.saveToken(riderId, token) }

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("Firebase Token", msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }


    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            homeViewModel.fetchOrders(riderId)
        }
    }
}