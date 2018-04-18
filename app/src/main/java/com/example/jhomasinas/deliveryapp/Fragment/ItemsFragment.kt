package com.example.jhomasinas.deliveryapp.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jhomasinas.deliveryapp.R


/**
 * A simple [Fragment] subclass.
 */
class ItemsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_items, container, false)
    }

}// Required empty public constructor
