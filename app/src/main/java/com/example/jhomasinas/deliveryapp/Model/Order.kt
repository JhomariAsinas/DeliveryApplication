package com.example.jhomasinas.deliveryapp.Model

import java.util.ArrayList

/**
 * Created by JhomAsinas on 4/18/2018.
 */
data class Order(
        var customer_name: String,
        var customer_address: String,
        var customer_contact: String,
        var product : ArrayList<ProductOrder>)