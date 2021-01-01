package com.example.BankOfBlood.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.BankOfBlood.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_requests.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    val requestsArray  = arrayListOf<String>()
    var adapter: RequestAdapter? = null
    val recyclerView: RecyclerView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBarRequests.visibility = View.VISIBLE
       linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL,false)
        requestRecyclerView.layoutManager = linearLayoutManager
        adapter = RequestAdapter(requestsArray)
        requestRecyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL)
        recyclerView?.addItemDecoration(dividerItemDecoration)
        requestsArray.clear()

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Requests made")
        val valueEvenListener = object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.exists())
                {

                    for(ds: DataSnapshot in datasnapshot.children)
                    {
                        requestsArray.add(ds.key.toString())
                        progressBarRequests.visibility = View.INVISIBLE
                        adapter?.notifyDataSetChanged()
                        Log.d("Requests", "the key is  "+ds.key.toString())
                    }
                }
                else
                {   progressBarRequests.visibility = View.INVISIBLE
                    Snackbar.make(view,"The requests you make will appear here!",Snackbar.LENGTH_INDEFINITE).setAction("Ok"){

                    }
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show()


                }
            }

        }
        reference.addListenerForSingleValueEvent(valueEvenListener)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RequestsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RequestsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}