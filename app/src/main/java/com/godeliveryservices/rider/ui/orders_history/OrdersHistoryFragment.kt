package com.godeliveryservices.rider.ui.orders_history

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.godeliveryservices.rider.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.dialog_order_filters.*
import kotlinx.android.synthetic.main.fragment_orders_history.*
import java.text.SimpleDateFormat
import java.util.*


class OrdersHistoryFragment : Fragment() {

    private lateinit var ordersHistoryViewModel: OrdersHistoryViewModel
    private val tabPosition by lazy { arguments?.getInt(ARG_TAB_POSITION) ?: 0 }
    private lateinit var viewPager: ViewPager2

    companion object {
        const val ARG_TAB_POSITION = "ARG_TAB_POSITION"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ordersHistoryViewModel =
            ViewModelProviders.of(requireActivity()).get(OrdersHistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_orders_history, container, false)
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        viewPager = root.findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.setCurrentItem(tabPosition, true)
        val tabs: TabLayout = root.findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = when (position) {
//                0 -> "Pending"
                0 -> "Active"
                1 -> "Delivered"
                else -> throw IllegalStateException("Invalid Tab Position")
            }
        }.attach()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        init()
    }

    private fun init() {
        setDefaultFilters()
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter_order) {
            showFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupObservers() {
//        ordersHistoryViewModel.responseMessagePending.observe(
//            viewLifecycleOwner,
//            androidx.lifecycle.Observer { message ->
//                if (viewPager.currentItem == 0)
//                    Snackbar.make(order_history_layout, message, Snackbar.LENGTH_LONG).show()
//            })
    }

    private fun showFilterDialog() {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_order_filters)

        //Restore data
        val filters = ordersHistoryViewModel.orderFilters.value
        filters?.startDate?.let { dialog.start_date_text.setText(it) }
        filters?.endDate?.let { dialog.end_date_text.setText(it) }

        val applyButton: Button = dialog.findViewById(R.id.apply_button)
        applyButton.setOnClickListener {
            // Save Data
            filters?.startDate = dialog.start_date_text.text.toString()
            filters?.endDate = dialog.end_date_text.text.toString()

            ordersHistoryViewModel.orderFilters.value = filters

            dialog.dismiss()
        }

        val clearButton: Button = dialog.findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
            // Clear Data
            setDefaultFilters()

            dialog.dismiss()
        }

        val startDateCalendar: Calendar = Calendar.getInstance()
        filters?.let {
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
            startDateCalendar.time = dateFormat.parse(filters.startDate)
        }

        val startDateView: EditText = dialog.findViewById(R.id.start_date_text)
        val startDateListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                startDateCalendar.set(Calendar.YEAR, year)
                startDateCalendar.set(Calendar.MONTH, monthOfYear)
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                startDateView.setText(
                    SimpleDateFormat(
                        "dd-MMM-yyyy",
                        Locale.US
                    ).format(startDateCalendar.time)
                )
            }

        startDateView.setOnClickListener {
            DatePickerDialog(
                requireContext(), startDateListener, startDateCalendar
                    .get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH),
                startDateCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val endDateCalendar: Calendar = Calendar.getInstance()
        filters?.let {
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
            endDateCalendar.time = dateFormat.parse(filters.endDate)
        }

        val endDateView: EditText = dialog.findViewById(R.id.end_date_text)
        val endDateListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                endDateCalendar.set(Calendar.YEAR, year)
                endDateCalendar.set(Calendar.MONTH, monthOfYear)
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                endDateView.setText(
                    SimpleDateFormat(
                        "dd-MMM-yyyy",
                        Locale.US
                    ).format(endDateCalendar.time)
                )
            }

        endDateView.setOnClickListener {
            DatePickerDialog(
                requireContext(), endDateListener, endDateCalendar
                    .get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH),
                endDateCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dialog.show()
        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    private fun setDefaultFilters() {
        val calendar = Calendar.getInstance()
        val formattedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(calendar.time)

        ordersHistoryViewModel.orderFilters.value = OrderFilters(
            startDate = formattedDate,
            endDate = formattedDate
        )
    }
}

data class OrderFilters(
    var startDate: String,
    var endDate: String
)