package com.godeliveryservices.rider.ui.cash_reports

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.repository.PreferenceRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_order_filters.*
import kotlinx.android.synthetic.main.fragment_cash_reports.*
import kotlinx.android.synthetic.main.fragment_cash_reports.view.*
import java.text.SimpleDateFormat
import java.util.*


class CashReportsFragment : Fragment(), OnListFragmentInteractionListener {
    // TODO: Customize parameters
    private var columnCount = 1

    private lateinit var cashReportsViewModel: CashReportsViewModel
    private val recyclerViewAdapter =
        CashReportsRecyclerViewAdapter(
            arrayListOf(),
            this@CashReportsFragment
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cash_reports, container, false)
        with(root.list) {
            layoutManager = when {
                columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(context)
                else -> androidx.recyclerview.widget.GridLayoutManager(context, columnCount)
            }
            adapter = this@CashReportsFragment.recyclerViewAdapter
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupObservers()
    }

    private fun init() {
        cashReportsViewModel =
            ViewModelProviders.of(this).get(CashReportsViewModel::class.java)
        setDefaultFilters()
    }

    private fun fetchData() {
        cashReportsViewModel.fetchOrders(PreferenceRepository(requireContext()).getRiderId())
    }

    private fun setupObservers() {

        cashReportsViewModel.orders.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { orders ->
                total_cash_text.text = "AED ${orders.first().Cash}"
                recyclerViewAdapter.setValues(orders)
            })

        cashReportsViewModel.showLoading.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { flag ->
                loading.visibility = if (flag) View.VISIBLE else View.GONE
            })

        cashReportsViewModel.responseMessage.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { message ->
                resetData()
                Snackbar.make(content_cash_reports, message, Snackbar.LENGTH_LONG).show()
            })

        cashReportsViewModel.orderFilters.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { filters ->
                fetchData()
            })
    }

    private fun resetData() {
        total_cash_text.text = "AED 0.0"
        recyclerViewAdapter.setValues(emptyList())
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

    override fun onListFragmentInteraction(item: Order?) {

    }

    private fun showFilterDialog() {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_order_filters)

        //Restore data
        val filters = cashReportsViewModel.orderFilters.value
        filters?.startDate?.let { dialog.start_date_text.setText(it) }
        filters?.endDate?.let { dialog.end_date_text.setText(it) }

        val applyButton: Button = dialog.findViewById(R.id.apply_button)
        applyButton.setOnClickListener {
            // Save Data
            filters?.startDate = dialog.start_date_text.text.toString()
            filters?.endDate = dialog.end_date_text.text.toString()

            cashReportsViewModel.orderFilters.value = filters

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

        cashReportsViewModel.orderFilters.value = OrderFilters(
            startDate = formattedDate,
            endDate = formattedDate
        )
    }
}

data class OrderFilters(
    var startDate: String,
    var endDate: String
)