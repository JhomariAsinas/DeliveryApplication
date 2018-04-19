package com.example.jhomasinas.deliveryapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.example.jhomasinas.deliveryapp.Adapter.OrderAdapter
import com.example.jhomasinas.deliveryapp.Config.OrderApi
import com.example.jhomasinas.deliveryapp.Config.OrderResponse
import com.example.jhomasinas.deliveryapp.Model.Order
import com.example.jhomasinas.deliveryapp.Model.ProductOrder
import kotlinx.android.synthetic.main.activity_order_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderList : AppCompatActivity(),OrderAdapter.Delegate {
    override fun onViewDetails(order: Order) {
        arrayProduct = order.product
        startActivity<OrderDetail>(
                "Name" to order.customer_name,
                "Address" to order.customer_address,
                "Contact" to order.customer_contact
        )
    }

    override fun onViewMap(order: Order) {
        finish()
    }

    companion object {
        var arrayProduct : ArrayList<ProductOrder>? = null
    }

    private var recyclerView1: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        recyclerView1 = findViewById(R.id.recyclerOrder)
        recyclerView1?.setHasFixedSize(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "List of Delivery"
        recyclerView1?.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
        getOrder()


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getOrder(){
        val apiservice = OrderApi.create()
        val call = apiservice.getOrder()

        call.enqueue(object: Callback<OrderResponse> {
            override fun onFailure(call: Call<OrderResponse>?, t: Throwable?) {
                toast("Failed To Retrieve Object")
            }

            override fun onResponse(call: Call<OrderResponse>?, response: Response<OrderResponse>?) {
                val orderList: ArrayList<Order> = response!!.body()!!.order!!
                val adapter = OrderAdapter(orderList,this@OrderList)
                recyclerView1?.adapter = adapter
                progressBar.visibility = View.INVISIBLE
            }

        })
    }
}

