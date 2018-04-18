package com.example.jhomasinas.deliveryapp

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
import kotlinx.android.synthetic.main.activity_order_list.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderList : AppCompatActivity() {


    private var recyclerView1: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        recyclerView1 = findViewById(R.id.recyclerOrder)
        recyclerView1?.setHasFixedSize(true)
        recyclerView1?.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
        getOrder()
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
                val adapter = OrderAdapter(orderList)
                recyclerView1?.adapter = adapter
                progressBar.visibility = View.INVISIBLE
            }

        })
    }
}
