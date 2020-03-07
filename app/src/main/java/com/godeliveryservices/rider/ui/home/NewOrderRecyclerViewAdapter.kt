package com.godeliveryservices.rider.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.godeliveryservices.rider.R
import com.godeliveryservices.rider.dummy.DummyContent.DummyItem
import com.godeliveryservices.rider.model.Order
import kotlinx.android.synthetic.main.list_item_new_order.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class NewOrderRecyclerViewAdapter(
    private var mValues: List<Order>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<NewOrderRecyclerViewAdapter.ViewHolder>() {

    private val mOnStatusClickListener: View.OnClickListener

    init {
        mOnStatusClickListener = View.OnClickListener { v ->
            val item = v.tag as Order
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onStatusClickListener(item)
        }
    }

    fun setValues(orders: List<Order>) {
        mValues = orders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_new_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
//        holder.mIdView.text = item.id
//        holder.mContentView.text = item.content

        with(holder.mView.accept_order_button) {
            tag = item
            setOnClickListener(mOnStatusClickListener)
        }

        holder.mView.branch_name_text.text = item.BranchName
        holder.mView.branch_contact_text.text = item.BranchContact
        holder.mView.branch_contact_text.setOnClickListener {
            mListener?.onBranchContactClickListener(item)
        }
        holder.mView.customer_name_text.text = item.CustomerName
        holder.mView.customer_phone_text.text = item.CustomerNumber
        holder.mView.customer_phone_text.setOnClickListener {
            mListener?.onCustomerContactClickListener(item)
        }
        holder.mView.delivery_address_text.text = item.CustomerAddress
        holder.mView.order_description_text.text = item.OrderDetails
        holder.mView.order_number_text.text =
            holder.mView.resources.getString(R.string.format_order_number, item.OrderID)
        holder.mView.order_price_text.text = "AED ${item.Amount}"
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
//        val mIdView: TextView = mView.item_number
//        val mContentView: TextView = mView.content

//        override fun toString(): String {
//            return super.toString() + " '" + mContentView.text + "'"
//        }
    }
}


/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 *
 *
 * See the Android Training lesson
 * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
 * for more information.
 */
interface OnListFragmentInteractionListener {
    // TODO: Update argument type and name
    fun onStatusClickListener(item: Order)

    fun onNavigationClickListener(item: Order)
    fun onBranchContactClickListener(item: Order)
    fun onCustomerContactClickListener(item: Order)
}
