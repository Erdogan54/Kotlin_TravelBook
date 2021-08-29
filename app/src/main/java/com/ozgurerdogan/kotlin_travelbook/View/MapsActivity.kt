package com.ozgurerdogan.kotlin_travelbook.View

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.room.Room
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ozgurerdogan.kotlin_travelbook.Database.AppDatabase
import com.ozgurerdogan.kotlin_travelbook.Database.Place
import com.ozgurerdogan.kotlin_travelbook.Database.PlaceDao
import com.ozgurerdogan.kotlin_travelbook.R
import com.ozgurerdogan.kotlin_travelbook.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager:LocationManager
    private lateinit var locationListener:LocationListener
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private var latitude:Double?=null
    private var longitude:Double?=null
    private var name:String?=null

    private lateinit var db:AppDatabase
    private lateinit var placeDao: PlaceDao
    private lateinit var selectedPlace: Place

    private var a=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "Places")
            .allowMainThreadQueries()
            .build()

        placeDao = db.placeDao()



    }

    fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    if (Build.VERSION.SDK_INT >= 28) {
                        locationManager.isLocationEnabled

                    }else{
                        //locationManager.getGpsStatus()
                    }

                    val last=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (last!=null){
                        val latlng=LatLng(last.latitude,last.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))
                        mMap.addMarker(MarkerOptions().position(latlng))
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,10f,locationListener)

                }

            }else{
                Toast.makeText(this,"Permission needed",Toast.LENGTH_LONG).show()
            }

        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        locationManager=this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener= object : LocationListener{ // bu satır requestLocationsUpdates çalıştığında çağrılır.

            override fun onLocationChanged(location: Location) {

                if (a==0){
                    var latlng=LatLng(location.latitude,location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))
                    mMap.addMarker(MarkerOptions().position(latlng))
                    a=1
                }

            }

        }

        val intent=intent
        val info=intent.getStringExtra("info")

        if (info=="old"){
            mMap.clear()

            selectedPlace=intent.getSerializableExtra("place") as Place
            val latlng=LatLng(selectedPlace.latitude!!,selectedPlace.longitude!!)
            mMap.addMarker(MarkerOptions().position(latlng).title(selectedPlace.placeName))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))

            binding.saveBtn.visibility=View.GONE
            binding.nameTxt.visibility=View.GONE
            binding.deleteBtn.visibility=View.VISIBLE

        }else{
            binding.saveBtn.visibility=View.VISIBLE
            binding.nameTxt.visibility=View.VISIBLE
            binding.saveBtn.isEnabled=false
            binding.deleteBtn.visibility=View.GONE

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Needed permission",Snackbar.LENGTH_INDEFINITE).setAction("Give permission"){
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }else{
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }else{
                // izin var ise --tüm kodlar burada yazılacak.....

                mMap.clear()

                if (Build.VERSION.SDK_INT >= 28) {
                    val locationProviders=locationManager.isLocationEnabled
                    if (locationProviders){
                        println("location true")
                    }else{
                        println("location false")

                    }

                }else {
                    val locationProviders =
                        Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_MODE)
                    if (locationProviders != null) {
                        println("version 28 altı gps aktif")
                    } else {
                        println("version 28 altı gps pasif")
                    }


                }

                val last=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (last!=null){
                    val latlng=LatLng(last.latitude,last.longitude)
                    mMap.addMarker(MarkerOptions().position(latlng))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,10f,locationListener)

            }
        }

        }

    override fun onMapLongClick(p0: LatLng) {
        latitude=p0.latitude
        longitude=p0.longitude

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        binding.saveBtn.isEnabled=true

    }

    fun save(view: View){
        name=binding.nameTxt.text.toString()

        if (binding.nameTxt.text!=null ){
            if (latitude!=null && longitude!=null){

                val place=Place(name,latitude!!,longitude!!)
                placeDao.insertAll(place)


                val intent= Intent(applicationContext,ListActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }else{
                Toast.makeText(applicationContext,"Sorry could not get coordinates",Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(applicationContext,"Please enter place name",Toast.LENGTH_LONG).show()
        }



    }

    fun delete(view:View){

        placeDao.delete(selectedPlace)
        Toast.makeText(applicationContext,"Location deleted.",Toast.LENGTH_LONG).show()


        val intent= Intent(applicationContext,ListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

}