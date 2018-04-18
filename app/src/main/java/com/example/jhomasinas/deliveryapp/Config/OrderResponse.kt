package com.example.jhomasinas.deliveryapp.Config

import com.example.jhomasinas.deliveryapp.Model.Order
import com.google.gson.annotations.SerializedName

/**
 * Created by JhomAsinas on 4/18/2018.
 */
class OrderResponse {
    @SerializedName("order")
    var order: ArrayList<Order>? = null

}