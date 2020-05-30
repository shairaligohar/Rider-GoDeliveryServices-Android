package com.godeliveryservices.rider.ui.orders_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.repository.PreferenceRepository
import kotlinx.android.synthetic.main.fragment_tab_orders_history.*
import kotlinx.android.synthetic.main.fragment_tab_orders_history.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class OrdersHistoryTabFragment : Fragment(),
    OnListFragmentInteractionListener {

    private val riderId by lazy { PreferenceRepository(requireContext()).getRiderId() }

    // TODO: Customize parameters
    private var columnCount = 1

    private lateinit var pageViewModel: OrdersHistoryViewModel
    private val adapter by lazy { OrderHistoryRecyclerViewAdapter(emptyList(), this) }
    private val sectionNumber by lazy {
        arguments?.getInt(ARG_SECTION_NUMBER)
            ?: throw IllegalArgumentException("Unable to retrieve intent data")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel =
            ViewModelProviders.of(requireActivity()).get(OrdersHistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tab_orders_history, container, false)
        with(root.list) {
            layoutManager = when {
                columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(context)
                else -> androidx.recyclerview.widget.GridLayoutManager(context, columnCount)
            }
            adapter = this@OrdersHistoryTabFragment.adapter
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): OrdersHistoryTabFragment {
            return OrdersHistoryTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onListFragmentInteraction(item: Order?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupViews()
    }

    private fun fetchData() {
        when (sectionNumber) {
            0 -> fetchActiveOrders()
            1 -> fetchDeliveredOrders()
        }
    }

    private fun fetchActiveOrders() {
        pageViewModel.fetchOrders("Active", riderId)
    }

    private fun fetchDeliveredOrders() {
        pageViewModel.fetchOrders("Delivered", riderId)
    }

    private fun setupObservers() {
//        pageViewModel.pendingOrders.observe(viewLifecycleOwner, Observer { orders ->
//            if (sectionNumber == 0)
//                adapter.setValues(orders)
//        })
        pageViewModel.processingOrders.observe(viewLifecycleOwner, Observer { orders ->
            if (sectionNumber == 0) {
                adapter.setValues(orders)
                unavailable_text.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            }
        })
        pageViewModel.deliveredOrders.observe(viewLifecycleOwner, Observer { orders ->
            if (sectionNumber == 1) {
                adapter.setValues(orders)
                unavailable_text.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        pageViewModel.responseMessageProcessing.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { message ->
                if (sectionNumber == 0) {
//                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    unavailable_text.text = "No Active Orders"
                    unavailable_text.visibility = View.VISIBLE
                }
            })

        pageViewModel.responseMessageDelivered.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { message ->
                if (sectionNumber == 1) {
//                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    unavailable_text.text = "No Delivered Orders"
                    unavailable_text.visibility = View.VISIBLE
                }
            })

        pageViewModel.showLoading.observe(viewLifecycleOwner, Observer { flag ->
//            loading.visibility = if (flag) View.VISIBLE else View.GONE
            list_layout.isRefreshing = flag
        })

        pageViewModel.orderFilters.observe(viewLifecycleOwner, Observer { filters ->
            fetchData()
        })
    }

    private fun setupViews() {
        list_layout.setOnRefreshListener { fetchData() }
    }
}