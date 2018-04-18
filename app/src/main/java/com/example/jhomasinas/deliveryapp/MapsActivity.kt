package com.example.jhomasinas.deliveryapp

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlacePicker

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var currentLatLng : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            loadPlacePicker()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.setOnMapClickListener{this}
        setUpMap()

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        private const val PLACE_PICKER_REQUEST = 3

    }

    private fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener{location ->
            if(location != null){
                lastLocation = location
                currentLatLng = LatLng(location.latitude,location.longitude)
                placeMarkerOnMap(currentLatLng!!)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15.0f))
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng){
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)
        markerOptions.title(titleStr)
        map.addMarker(markerOptions)
    }

    private fun getAddress(latlng: LatLng): String{
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1)
             if(null != addresses && !addresses.isEmpty() ){
                 address = addresses[0]
                 for(i in 0 until address.maxAddressLineIndex){
                     addressText += if(i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                 }
             }
        } catch (e: IOException){
            Log.e("MapsActivity",e.localizedMessage)
        }
        return addressText
    }


    private fun loadPlacePicker(){
        val builder = PlacePicker.IntentBuilder()

        try {
            startActivityForResult(builder.build(this@MapsActivity), PLACE_PICKER_REQUEST)
        }catch (e: GooglePlayServicesNotAvailableException){
            e.printStackTrace()
        }catch (e: GooglePlayServicesNotAvailableException){
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                map.clear()
                val place = PlacePicker.getPlace(this,data)
                var addressText = place.name.toString()
                addressText += "\n" + place.address.toString()
                placeMarkerOnMap(place.latLng)
                // drawRoute(currentLatLng!!,place.latLng)
            }
        }
    }

    private fun getURL(from: LatLng , to: LatLng): String{
        val origin      = "origin=" + from.latitude + "," + from.longitude
        val destination = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params  = "$origin&$destination&$sensor"

        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    private fun decodePoly(encoded : String): List<LatLng>{
        val poly  = ArrayList<LatLng>()
        var index = 0
        val len   = encoded.length
        var lat   = 0
        var lng   = 0

        while (index < len){
            var b: Int
            var shift = 0
            var result = 0
              do {
                  b = encoded[index++].toInt() - 63
                  result = result or (b and 0x1f shl shift)
                  shift += 5
              } while (b >= 0x20)
            val dlat = if( result and 1 != 0 ) (result shr 1).inv() else result shr 1
            lat += dlat

            shift  = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            }while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly

    }

   /* private fun drawRoute(current: LatLng , place : LatLng){

        val LatLongB = LatLngBounds.Builder()

        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        val url = getURL(current,place)

            doAsync {
                val result = URL(url).readText()
                uiThread {
                    val parser = Parser()
                    val stringBuilder = StringBuilder(result)
                    val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                    val routes = json.array<JsonObject>("routes")
                    val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }

                    options.add(current)
                    LatLongB.include(current)
                    for(point in polypts){
                        options.add(point)
                        LatLongB.include(point)
                    }
                    options.add(place)
                    LatLongB.include(place)
                    val bounds = LatLongB.build()
                    map.addPolyline(options)
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))
            }
        }
    }*/
}
