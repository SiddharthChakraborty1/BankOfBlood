package com.example.BankOfBlood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private val TAG ="Siddharth"
    /*private lateinit var reference : DatabaseReference*/
    var email: String? = null
    var name: String? = null
    var phone: String? = null
    var uid: String? = null
    var password: String? = null
    var confPassword: String? = null
    var bloodGroup: String? = null
    var mAuth: FirebaseAuth? = null
    /*var database: FirebaseDatabase? = null
    var reference: DatabaseReference? = null*/
    val bloodArray = arrayListOf<String>()

    var spinner : Spinner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //here we will start the activity and see if it keeps running
        spinner = findViewById(R.id.spinnerBlood)
        auth = FirebaseAuth.getInstance()

       /* database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference()*/


        bloodArray.add("Select your blood group")
        bloodArray.add("O+")
        bloodArray.add("O-")
        bloodArray.add("A+")
        bloodArray.add("A-")
        bloodArray.add("B+")
        bloodArray.add("B-")
        bloodArray.add("AB+")
        bloodArray.add("AB-")


        //= arrayListOf<String>("Select your blood group","O+","O-","A+","A-","B+","B-","AB+","AB-")
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(this,R.layout.style_spinner,bloodArray)
        spinner?.adapter = arrayAdapter
        spinner?.onItemSelectedListener = object :


            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //
                var selectedItem: String = bloodArray.get(p2)
                if(!selectedItem.equals(bloodArray.get(0)))
                {
                    bloodGroup = bloodArray.get(p2)
                }




            }


        }
        tvSignupToLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        btnSignup.setOnClickListener {
           // Toast.makeText(applicationContext,"Button clicked",Toast.LENGTH_SHORT).show()
            name = etSignupName.text.toString()
            email = etSignupEmail.text.toString()
            phone = etSignupNumber.text.toString()
            password = etSignupPassword.text.toString()
            confPassword = etSignupConfPassword.text.toString()

            if(!password.equals(confPassword))
            {
                Toast.makeText(applicationContext,"Passwords do not match",Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Signup:password checked ")
            }
            else if(name!!.isEmpty())
            {
                etSignupName.setError("Cannot be empty")
                Log.i(TAG, "Name checked")


            }
            else if(email!!.isEmpty())
            {
                etSignupEmail.setError("Cannot be empty")
                Log.i(TAG, "Email checked")


            }
            else if(bloodGroup.equals(bloodArray.get(0)))
            {

                Toast.makeText(applicationContext,"Select a valid blood group",Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Blood array checked ")
            }
            else if(password!!.isEmpty())
            {
                etSignupName.setError("Cannot be empty")


            }
            else
            {
                Log.i(TAG, "Creating user ")
                Log.i(TAG, "onCreate: email is "+email)
                Log.i(TAG, "onCreate: passwords is "+password)
               val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        uid = auth.uid
                        val reference = FirebaseDatabase.getInstance().reference
                        reference.child("Users").child(uid!!).child("Name").setValue(name).addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                Toast.makeText(applicationContext,"Stored successfully",Toast.LENGTH_SHORT).show()

                            }
                            else
                            {
                                Toast.makeText(applicationContext,it.exception?.message,Toast.LENGTH_SHORT).show()
                            }
                        }
                        reference.child("Users").child(uid!!).child("Blood Group").setValue(bloodGroup!!)
                        reference.child("Users").child(uid!!).child("Email").setValue(email!!)
                        reference.child("Users").child(uid!!).child("Phone Number").setValue(phone!!)
                        reference.child("Blood Group").child(bloodGroup!!).child(uid!!).child("Email").setValue(email)

                        startActivity(Intent(applicationContext,UserActivity::class.java))

                    }
                    else
                    {
                        Toast.makeText(applicationContext,"Cannot create user",Toast.LENGTH_SHORT).show()
                        Toast.makeText(applicationContext,it.exception?.message,Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

    }


}