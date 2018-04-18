package com.example.jhomasinas.deliveryapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jhomasinas.deliveryapp.Config.OrderApi
import com.example.jhomasinas.deliveryapp.Config.OrderResponse
import com.example.jhomasinas.deliveryapp.Model.Order
import com.example.jhomasinas.deliveryapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class ItemsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_items, container, false)
    }

    fun getOrder(){
        val apiservice = OrderApi.create()
        val call = apiservice.getOrder()

        call.enqueue(object: Callback<OrderResponse>{
            override fun onFailure(call: Call<OrderResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<OrderResponse>?, response: Response<OrderResponse>?) {
               val orderList: ArrayList<Order> = response!!.body()!!.order!!
            }

        })
    }

}// Required empty public constructor
