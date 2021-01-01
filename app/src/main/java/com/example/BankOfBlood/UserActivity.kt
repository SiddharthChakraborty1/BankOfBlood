package com.example.BankOfBlood

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.translationMatrix

import androidx.fragment.app.Fragment
import com.example.BankOfBlood.fragments.AddRequestFragment
import com.example.BankOfBlood.fragments.DashboardFragment
import com.example.BankOfBlood.fragments.RequestsFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.ANIMATION_MODE_FADE

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user.*
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest
import com.google.android.material.snackbar.Snackbar.make as make1

class UserActivity : AppCompatActivity() {
    private val dashboardFragment = DashboardFragment()
    private val requestFragment = RequestsFragment()
    private val addRequestsFragment = AddRequestFragment()
    var locationManager : LocationManager? = null
    lateinit var locationListener : LocationListener
     var user: FirebaseUser? = null
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        user = FirebaseAuth.getInstance().currentUser
        mAuth = FirebaseAuth.getInstance()

       /* The following code will set upt the location manager and location listener
       * to get the user's location from the gps provider*/

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        locationListener = object: LocationListener{
            override fun onLocationChanged(p0: Location) {
                var latitude: Double = p0.latitude
                var longitude: Double = p0.longitude
               var cityName: String? =  getCityName(latitude,longitude)
                //Toast.makeText(applicationContext,"The city name is $cityName",Toast.LENGTH_SHORT).show()
                var locRef = FirebaseDatabase.getInstance().getReference("Users").child(user?.uid.toString()).child("Location")
                locRef.child("Latitude").setValue(latitude.toString())
                locRef.child("Longitude").setValue(longitude.toString()).addOnCompleteListener {
                    if(it.isSuccessful)
                    { Toast.makeText(applicationContext,"Location Stored successfully",Toast.LENGTH_SHORT).show()
                        createNotification()
                        locationManager?.removeUpdates(locationListener)

                    }
                }
            }

        }

        /*
       The following code checks whether the user's location is stored in the database.
       if it's not then it calls the getLastLocation() method to get the user's location
       and store it in the firebase database
        */
        var locationRef = FirebaseDatabase.getInstance().getReference("Users").child(user?.uid.toString()).child("Location")

        val eventListener = object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                   // Snackbar.make(fragment_container,"Location is already saved",Snackbar.LENGTH_SHORT).show()

                }
                else
                {
                    /*Snackbar.make(fragment_container,"Location not saved",Snackbar.LENGTH_SHORT).show()*/
                    Snackbar.make(fragment_container,"Your location is being saved, please wait!",Snackbar.LENGTH_INDEFINITE).setAnimationMode(
                        BaseTransientBottomBar.ANIMATION_MODE_FADE)
                        .setAction("Action"){
                            Toast.makeText(applicationContext,"You will be notified when location is saved",Toast.LENGTH_SHORT).show()
                        }.show()
                    if(ContextCompat.checkSelfPermission(this@UserActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(this@UserActivity, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
                    }
                    else
                    {
                        getLastLocation()
                    }

                }
            }

        }
        locationRef.addListenerForSingleValueEvent(eventListener)

        // The following code will set the default fragment to be the dashBoard fragment

        replaceFragment(dashboardFragment)


    // the following code will set up the custom navigation bar


        navBar.setOnItemSelectedListener {
            when(it)
            {
                R.id.nav_dashboad ->
                    replaceFragment(dashboardFragment)
                R.id.nav_request ->
                    replaceFragment(addRequestsFragment)
                R.id.nav_requests ->
                    replaceFragment(requestFragment)

            }
        }


    }
// The followinf method will take latitude and longitude and return a string that will give you the city name
    fun getCityName(lat: Double, lng: Double): String
    {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = geocoder.getFromLocation(lat,lng,1)
        val cityName = address.get(0).getAddressLine(1)

        if(cityName == null) {
            Log.d("City", "City not found")
            return "Not found"
        }
        else
        {
            Log.d("City", "The city is $cityName")
            return cityName
        }

    }
    // The following function will take care of the reuqest permissions prompted by android

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if(grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(
                        fragment_container,
                        "Location permission granted",
                        Snackbar.LENGTH_SHORT
                    ).setAnimationMode(
                        BaseTransientBottomBar.ANIMATION_MODE_FADE
                    ).show()
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0F,
                        locationListener
                    )
                }
            }

    }

    // the following method is used to replace the fragments in the frame layout

    private fun replaceFragment(fragment: Fragment)
    {
        if(fragment != null)
        {
            val transcation = supportFragmentManager.beginTransaction()
            transcation.replace(R.id.fragment_container,fragment)
            transcation.commit()
        }

    }

    // the following two method takes care of the top drop down menu

    //1

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //2

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.topMenuLogout ->
            {
                if(user != null)
                {

                    Snackbar.make(fragment_container,"Logging out",Snackbar.LENGTH_SHORT).show()
                    mAuth?.signOut()
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
            }
            R.id.topMenuSnackbar ->
            {
                Snackbar.make(fragment_container,"This is a sample snackbar",Snackbar.LENGTH_INDEFINITE).setAnimationMode(
                    BaseTransientBottomBar.ANIMATION_MODE_FADE).setBackgroundTint(Color.parseColor("#A4508B"))
                    .setAction("Action"){
                        Toast.makeText(this,"Snackbar button clicked",Toast.LENGTH_SHORT).show()
                    }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

   /* The following method uses the location manager and location listener to get the user's
    current location via gps*/
    private fun getLastLocation()
    {


        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,0L,0F,locationListener)

            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
            }


        }
        catch (e: Exception)
        {
            e.printStackTrace()

        }
    }
    fun createNotification()
    {
        val channelId = "com.example.BankOfBlood"
        val channelName = "BankOfBlood"
        val description = "You are ready to make a request!"
        val notificationId = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Ready to go!")
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_done)
                .build()

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationId, notification)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}