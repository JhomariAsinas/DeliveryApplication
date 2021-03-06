package com.example.jhomasinas.deliveryapp.Config

import com.example.jhomasinas.deliveryapp.Model.Order
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created by JhomAsinas on 4/18/2018.
 */
interface OrderApi {

    @GET("admin/getOrderCash")
        fun getOrder(): Call<OrderResponse>


    companion object {
        val BASE_URL = "http://192.168.1.50/mobilecom/"
        fun create(): OrderApi {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(
                            OkHttpClient().newBuilder().addInterceptor(
                                    HttpLoggingInterceptor().apply{ this.level = HttpLoggingInterceptor.Level.BODY}
                            ).build()
                    )
                    .build()
            return retrofit.create(OrderApi::class.java)
        }
    }
}