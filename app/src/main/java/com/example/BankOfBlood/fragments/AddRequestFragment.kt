package com.example.BankOfBlood.fragments

import RecyclerAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

import android.content.DialogInterface
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.BankOfBlood.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_add_request.*
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [AddRequestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddRequestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var userLocation: Location? = null
    var adapter: RecyclerAdapter? = null

    private lateinit var linearLayoutManager : LinearLayoutManager
    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val bloodArray = arrayListOf<String>()
    val names = arrayListOf<String>()
    val emails = arrayListOf<String>()
    val numbers = arrayListOf<String>()
   var arrayAdapter: ArrayAdapter<String>? = null
    var bloodGroup: String? = null
    val donorsArray = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        bloodArray.add("Select blood group")
        bloodArray.add("O+")
        bloodArray.add("O-")
        bloodArray.add("A+")
        bloodArray.add("A-")
        bloodArray.add("B+")
        bloodArray.add("B-")
        bloodArray.add("AB+")
        bloodArray.add("AB-")




        return inflater.inflate(R.layout.fragment_add_request, container, false)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL,false)
        recyclerView.layoutManager = linearLayoutManager
        adapter = RecyclerAdapter(donorsArray)
        recyclerView.adapter = adapter

        arrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,resources.getStringArray(R.array.blood_groups))
        spinnerRequest.adapter = arrayAdapter


        spinnerRequest.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               bloodGroup = spinnerRequest.selectedItem.toString()
                Log.d("selected blood group", "the selected blood group is $bloodGroup")
            }


        }

        btnAddRequest.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            donorsArray.clear()
            adapter?.notifyDataSetChanged()
            getUserLocation(view)

        }

    }



    // the following function will go into the blood group table and
    // get the user id of the users of the particular blood group that is mentioned in the request
    private fun getPossibleDonors()
    {  val TAG = "First request"
        Log.d(TAG, "Inside getPossibleDonors()")
        val reference = FirebaseDatabase.getInstance().reference.child("Blood Group").child(bloodGroup!!)

       val valueEventListener = object: ValueEventListener{
           override fun onCancelled(p0: DatabaseError) {

           }

           override fun onDataChange(p0: DataSnapshot) {

               try {


                   for (ds: DataSnapshot in p0.children) {
                       if (ds.exists())
                       {  Log.d(TAG, "finding users with the same blood group")

                           Log.d(TAG, "calling function checkDistance()")
                           checkDistance(ds.key.toString())
                       }
                       else
                       {
                           Log.d("Donors", "DataSnapshot does not exist")
                       }
                   }
               }
               catch (e: Exception)
               {
                   e.printStackTrace()
               }


           }

       }

        reference.addListenerForSingleValueEvent(valueEventListener)
    }

    fun checkDistance(id: String)
    {
        val TAG = "First request"
        Log.d(TAG, "Inside checkDistance()")
        var donorExists = false

        var donorLocation: Location? = null
        var distance = 100f


        // now we have the user's location then we will check it with the id's location

        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(id)
        val valueEventListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(snapshot: DataSnapshot in p0.children)
                {

                    if(snapshot.child("Latitude").exists())
                    {
                       // Toast.makeText(requireContext(),"Donor exists",Toast.LENGTH_SHORT).show()
                        donorExists = true

                        Log.d(TAG, "Setting up donor location")
                        val donorLat = snapshot.child("Latitude").value.toString()
                        Log.d("Siddharth", "the donor latitude is "+donorLat)
                        val donorLatitude = donorLat.toDouble()



                        val donorLong = snapshot.child("Latitude").value.toString()
                        val donorLongitude = donorLong.toDouble()
                        donorLocation = Location("")
                        donorLocation?.latitude = donorLatitude
                        donorLocation?.longitude = donorLongitude


                        Log.d(TAG, "Calculating distance between donor and user")
                         distance  = userLocation?.distanceTo(donorLocation)!!
                        distance /= 1000
                       // Toast.makeText(requireContext(),"The distance between user and donor in meters is "+ distance!!.div(1000).toString(),Toast.LENGTH_SHORT).show()
                        if(distance <= 50)
                        {    Log.d(TAG, "Distance less than 50km adding in array")
                            donorsArray.add(id)
                           // getValues(id)
                            adapter?.notifyDataSetChanged()
                            progressBar.visibility = View.INVISIBLE


                            Log.i("Recycler View","The element "+id+" has been added to the array")
                            Log.i("Recycler View","The size of the array is "+donorsArray.size.toString())


                        }



                    }
                }
            }

        }
        reference.addListenerForSingleValueEvent(valueEventListener)



    }


    companion object {


        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddRequestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
    fun getUserLocation(view: View)
    {
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            val userId = user.uid
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
            val eventListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                   if(p0.exists())
                   {
                       if(p0.child("Location").exists())
                       {
                            Toast.makeText(requireContext(),"User location exists",Toast.LENGTH_SHORT).show()
                           val userLat: String? = p0.child("Location").child("Latitude").getValue().toString()
                           val userLatitude = userLat?.toDouble()
                           val userLong: String? = p0.child("Location").child("Latitude").getValue().toString()
                           val userLongitude = userLong?.toDouble()
                           userLocation = Location("")
                           if (userLatitude != null) {
                               userLocation?.latitude = userLatitude
                           }
                           if (userLongitude != null) {
                               userLocation?.longitude = userLongitude
                           }
                           getDetails(view)

                       }
                       else
                       {
                           val dialogBuilder = AlertDialog.Builder(activity!!)
                           dialogBuilder.setMessage("Your location is being saved, please try again in a few minutes")
                               .setTitle("Location not saved yet!")
                               .setCancelable(true)
                               .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                                   dialogInterface.dismiss()
                               })

                           val alert = dialogBuilder.create()
                           alert.show()
                           progressBar?.visibility = View.INVISIBLE

                       }
                   }
                }

            }
            ref.addListenerForSingleValueEvent(eventListener)
        }
        else
        {
            Toast.makeText(requireContext(),"The curent user doesn't exist",Toast.LENGTH_SHORT).show()
        }
    }

    fun getDetails(view: View)
    {
        requestFragmentCardView.animate().translationY(0F)


        val locationRef = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Location")
        val  listener = object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    if(bloodGroup != "Select Blood Group")
                    {   val TAG = "First Request"
                        Log.d(TAG, "blood group selected")

                        val reference = FirebaseDatabase.getInstance().getReference().child("Requests").push()
                        val requestKey = reference.key.toString()
                        Log.d(TAG, "Request key generated")
                        Log.d("Request key ","The key of the request is $requestKey")
                        // uid = FirebaseAuth.getInstance().currentUser?.uid
                        val requestReference = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Requests made")
                            .child(requestKey)
                        requestReference.child("Blood Group Requested").setValue(bloodGroup)
                        Log.d(TAG, "blood group inserted into database")
                        reference.child("User Id").setValue(uid).addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                Snackbar.make(view,"Request added successfully",Snackbar.LENGTH_SHORT).show()
                                Log.d(TAG, "User id inserted into database")
                                createNotification()
                            }
                        }
                        reference.child("Blood Group Required").setValue(bloodGroup).addOnCompleteListener {
                            if(it.isSuccessful)
                            {    Log.d(TAG, "Now calling function getPossibleDonors()")
                                getPossibleDonors()
                            }
                            val currentDate = LocalDateTime.now()
                            val date = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            reference.child("Date").setValue(date)

                        }
                    }
                }
                else
                {

                    val dialogBuilder = AlertDialog.Builder(activity!!)
                    dialogBuilder.setMessage("Your location is being saved, please try again in a few minutes")
                        .setTitle("Location not saved yet!")
                        .setCancelable(true)
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })

                    val alert = dialogBuilder.create()
                    alert.show()


                }
            }

        }

        locationRef.addListenerForSingleValueEvent(listener)
    }

    fun createNotification()
    {
        val channelId = "com.example.BankOfBlood"
        val channelName = "BankOfBlood"
        val description = "Your request has been added successfully"
        val notificationId = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)

            val notification = NotificationCompat.Builder(activity!!, channelId)
                .setContentTitle("Ready to go!")
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_done)
                .build()

            val notificationManager = NotificationManagerCompat.from(activity!!)
            notificationManager.notify(notificationId, notification)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}