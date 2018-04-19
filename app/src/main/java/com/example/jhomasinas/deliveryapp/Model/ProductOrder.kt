package com.example.jhomasinas.deliveryapp.Model

import android.os.Parcelable

/**
 * Created by JhomAsinas on 4/19/2018.
 */
data class ProductOrder(
        var product_name  : String,
        var product_amount: String,
        var product_items : String,
        var product_image : String
)