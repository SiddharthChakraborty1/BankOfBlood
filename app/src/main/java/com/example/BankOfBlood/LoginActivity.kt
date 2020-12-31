package com.example.BankOfBlood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var name : String? = null
    val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val user: FirebaseUser? = mAuth.currentUser
        if(user!=null)
        {
            startActivity(Intent(applicationContext,UserActivity::class.java))
            finish()
        }

        tvSignup.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        })


    }

    fun Login(view: View)
    {
        if(etLoginEmail.text.toString().isEmpty())
        {

            etLoginEmail.setError("Enter your name")
        }
        else if(etLoginPassword.text.toString().isEmpty())
        {
            etLoginPassword.setError("Cannot be empty")

        }
        else
        {
            mAuth.signInWithEmailAndPassword(etLoginEmail.text.toString(), etLoginPassword.text.toString()).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Toast.makeText(this,"Logged in successfully",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext,UserActivity::class.java))
                    finish()
                }
                else
                {
                    Toast.makeText(this,"Something went wrong, try again",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}