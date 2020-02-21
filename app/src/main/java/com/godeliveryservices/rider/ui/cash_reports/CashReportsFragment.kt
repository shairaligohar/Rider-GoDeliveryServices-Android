package com.godeliveryservices.rider.ui.cash_reports

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.dummy.DummyContent
import kotlinx.android.synthetic.main.fragment_cash_reports.view.*

class CashReportsFragment : Fragment(), OnListFragmentInteractionListener {
    // TODO: Customize parameters
    private var columnCount = 1

    private lateinit var cashReportsViewModel: CashReportsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cashReportsViewModel =
            ViewModelProviders.of(this).get(CashReportsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cash_reports, container, false)
        with(root.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter =
                CashReportsRecyclerViewAdapter(
                    DummyContent.ITEMS,
                    this@CashReportsFragment
                )
        }
        return root
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {

    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }
}