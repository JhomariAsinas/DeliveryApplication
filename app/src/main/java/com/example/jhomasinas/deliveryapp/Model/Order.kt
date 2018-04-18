package com.example.jhomasinas.deliveryapp.Model

/**
 * Created by JhomAsinas on 4/18/2018.
 */
data class Order(
        var customer_name: String,
        var customer_address: String,
        var customer_contact: String,
        var customer_amount:String,
        var product_name: String,
        var product_code: String)