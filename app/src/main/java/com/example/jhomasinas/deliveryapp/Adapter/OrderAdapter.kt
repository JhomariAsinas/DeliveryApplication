package com.example.jhomasinas.deliveryapp.Adapter


import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beust.klaxon.JsonArray
import com.example.jhomasinas.deliveryapp.Model.Order
import com.example.jhomasinas.deliveryapp.R

/**
 * Created by JhomAsinas on 4/18/2018.
 */
class OrderAdapter(val orderList: List<Order>): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_order,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

}