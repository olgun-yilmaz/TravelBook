package com.olgunyilmaz.travelbook.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.olgunyilmaz.travelbook.R
import com.olgunyilmaz.travelbook.databinding.ActivityMapsBinding
import com.olgunyilmaz.travelbook.model.Place
import com.olgunyilmaz.travelbook.roomdb.PlaceDAO
import com.olgunyilmaz.travelbook.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db : PlaceDatabase
    private lateinit var placeDAO: PlaceDAO
    private lateinit var compositeDisposable: CompositeDisposable

    var trackBoolean : Boolean? = null
    private var selectedLongitude : Double? = 0.0
    private var selectedLatitude : Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        sharedPreferences = this.getSharedPreferences("com.olgunyilmaz.travelbook", MODE_PRIVATE)
        compositeDisposable = CompositeDisposable()

        db = Room.databaseBuilder(applicationContext,PlaceDatabase ::class.java, "Places").build()
        placeDAO = db.placeDao()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap // 40.98781794724289, 29.036847381117376 : saracoglu
        mMap.setOnMapLongClickListener(this@MapsActivity)

        // casting
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                trackBoolean = sharedPreferences.getBoolean("trackBoolean",false)
                if (! trackBoolean!!){
                    val userLoc = LatLng(location.latitude,location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,15f))
                    mMap.addMarker(MarkerOptions().position(userLoc).title("Current Location"))
                    sharedPreferences.edit().putBoolean("trackBoolean",true).apply()
                }
            }

        }

        requestPermission()



    }

    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){ // permission granted
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10000,100f,locationListener)
                }
            }else{ // denied
                Toast.makeText(this@MapsActivity,"Permission needed.",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED  ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"Permission needed for location!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            // permission granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100,10f,locationListener)
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (lastLocation != null) {
                val lastUserLoc = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLoc,15f))
                mMap.isMyLocationEnabled = true
            }
        }

    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))

        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
    }

    fun save(view : View){

        // main thread ui / default thread -> CPU, I/O Thread Internet/Database

        if (selectedLongitude != null && selectedLatitude != null){
            val name = binding.placeNameText.text.toString()
            val place = Place(name,selectedLatitude!!,selectedLongitude!!)
            compositeDisposable.add(
                placeDAO.insert(place)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this@MapsActivity :: handleResponse)
            )

        }


    }

    fun delete(view : View){
        println("deleted")
        handleResponse()
    }

    fun handleResponse(){
        val intent = Intent(this@MapsActivity, MainActivity :: class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

}