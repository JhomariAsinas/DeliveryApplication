package com.example.jhomasinas.deliveryapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.beust.klaxon.*
import com.example.jhomasinas.deliveryapp.Fragment.ItemsFragment
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.*
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        private const val PLACE_PICKER_REQUEST = 3

        private const val REQUEST_CHECK_SETTINGS = 2

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.setOnMapClickListener{this}
        setUpMap()
    }


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var currentLatLng : LatLng? = null
    private lateinit var locationCallback : LocationCallback
    private lateinit var locationRequest  : LocationRequest
    private var locationUpdateState = false
    private var progressDialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        val fab = findViewById<FloatingActionButton>(R.id.fab2)
        fab.setOnClickListener {
            loadPlacePicker()
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                map.clear()
                placeMarkerOnMap(LatLng(lastLocation.latitude,lastLocation.longitude))
            }
        }


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                startActivityForResult(intentFor<OrderList>(),60)
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    fun setUpMap(){
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


    fun placeMarkerOnMap(location: LatLng){
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)
        markerOptions.title(titleStr)
        map.addMarker(markerOptions)
    }

    fun getAddress(latlng: LatLng): String {
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

    fun loadPlacePicker(){
        val builder = PlacePicker.IntentBuilder()

        try {
            startActivityForResult(builder.build(this@MainActivity), PLACE_PICKER_REQUEST)
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
                drawRoute(currentLatLng!!,place.latLng)
            }
        }else if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                locationUpdateState = true
                startLocationUpdates()
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

   private fun drawRoute(current: LatLng , place : LatLng){
       progressDialog = indeterminateProgressDialog("Calculating Route")
       progressDialog?.setCancelable(false)
       progressDialog?.show()
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
                progressDialog?.dismiss()
            }
        }
    }



    private fun startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null)

    }

    private fun createLocationRequest(){
        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)

        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if(e is ResolvableApiException){
                try {
                    e.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                }catch (sendEx: IntentSender.SendIntentException){

                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if(!locationUpdateState){
            startLocationUpdates()
        }
    }

    fun addFragment(fragment: Fragment){
       supportFragmentManager
               .beginTransaction()
               .setCustomAnimations(R.anim.abc_slide_in_top,R.anim.abc_slide_out_bottom)
               .replace(R.id.content_main,fragment,fragment.javaClass.simpleName)
               .commit()
    }

}
