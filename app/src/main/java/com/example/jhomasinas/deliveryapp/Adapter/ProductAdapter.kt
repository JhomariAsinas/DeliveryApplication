package com.example.jhomasinas.deliveryapp.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.jhomasinas.deliveryapp.Model.ProductOrder
import com.example.jhomasinas.deliveryapp.R
import com.squareup.picasso.Picasso

/**
 * Created by JhomAsinas on 4/19/2018.
 */
class ProductAdapter(val prodlist: List<ProductOrder>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_product,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return prodlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val product : ProductOrder = prodlist[position]

        holder?.prodname?.text = product.product_name
        holder?.prodAmount?.text = "$"+product.product_amount+".00"
        Picasso.get()
                .load("http://192.168.1.50/e-commerce/assets/image/"+product.product_image)
                .resize(150,150)
                .centerCrop()
                .into(holder?.prodImg)
    }

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val prodname   = itemView.findViewById<TextView>(R.id.prodName)
        val prodAmount = itemView.findViewById<TextView>(R.id.prodAmount)
        val prodImg    = itemView.findViewById<ImageView>(R.id.imgProd)
    }

}