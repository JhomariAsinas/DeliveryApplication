package com.example.jhomasinas.deliveryapp.Adapter


import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.beust.klaxon.JsonArray
import com.example.jhomasinas.deliveryapp.Model.Order
import com.example.jhomasinas.deliveryapp.Model.ProductOrder
import com.example.jhomasinas.deliveryapp.R

/**
 * Created by JhomAsinas on 4/18/2018.
 */
class OrderAdapter(val orderList: List<Order>,val delegate: Delegate): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    interface Delegate {
        fun onViewMap    (order:Order)
        fun onViewDetails(order:Order)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val order: Order = orderList[position]

        holder?.customaddress?.text = order.customer_address
        holder?.customName?.text    = order.customer_name

        holder?.btnViewMap?.setOnClickListener {
           delegate.onViewMap(orderList.get(position))
        }

        holder?.btnViewDetails?.setOnClickListener {
            delegate.onViewDetails(orderList.get(position))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_order,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val customaddress  = itemView.findViewById<TextView>(R.id.textAddress)
        val customName     = itemView.findViewById<TextView>(R.id.autofitTextView1)
        val btnViewMap     = itemView.findViewById<Button>(R.id.btnMap)
        val btnViewDetails = itemView.findViewById<Button>(R.id.btnViewDetails)

    }

}