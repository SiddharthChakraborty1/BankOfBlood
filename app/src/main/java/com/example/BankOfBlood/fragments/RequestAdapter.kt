package com.example.BankOfBlood.fragments

import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.BankOfBlood.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.request_recycler_layout.view.*

class RequestAdapter(val requestsArray: ArrayList<String>) : RecyclerView.Adapter<RequestAdapter.ViewHolder> (){


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textViewBlood = itemView.findViewById(R.id.requestRecyclerTvBlood) as TextView
        val textViewDate = itemView.findViewById(R.id.requestRecyclerTvDate) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.request_recycler_layout,parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return requestsArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val requestId = requestsArray[position]
        var bloodGroupRequested : String? = null
        var date: String? = null
        val reference = FirebaseDatabase.getInstance().reference.child("Requests").child(requestId)
        val valueEventListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                {
                    bloodGroupRequested = dataSnapshot.child("Blood Group Required").value.toString()
                    holder.textViewBlood.text = "Blood group requested: $bloodGroupRequested"
                    date = dataSnapshot.child("Date").value.toString()
                    holder.textViewDate.text = "Request on: $date"
                }

            }

        }
        reference.addListenerForSingleValueEvent(valueEventListener)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}