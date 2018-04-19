package com.example.jhomasinas.deliveryapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.jhomasinas.deliveryapp.Adapter.ProductAdapter
import com.example.jhomasinas.deliveryapp.Model.ProductOrder
import org.jetbrains.anko.find

class OrderDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Order Detail"

        val array : ArrayList<ProductOrder> = OrderList.arrayProduct!!
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerItems)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        recyclerView.adapter = ProductAdapter(array)


        val textName = find<TextView>(R.id.customerName)
        val textAdd  = find<TextView>(R.id.customAddress)
        val textCon  = find<TextView>(R.id.customerContact)
        textName.text = intent.getStringExtra("Name")
        textAdd.text  = intent.getStringExtra("Address")
        textCon.text  = intent.getStringExtra("Contact")

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
