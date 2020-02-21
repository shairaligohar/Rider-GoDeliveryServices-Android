package com.godeliveryservices.rider.ui.orders_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.godeliveryservices.rider.R
import com.google.android.material.tabs.TabLayout

class OrdersHistoryFragment : Fragment() {

    private lateinit var ordersHistoryViewModel: OrdersHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ordersHistoryViewModel =
            ViewModelProviders.of(this).get(OrdersHistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_orders_history, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        ordersHistoryViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), parentFragmentManager)
        val viewPager: ViewPager = root.findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = root.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        return root
    }
}