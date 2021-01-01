import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.BankOfBlood.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecyclerAdapter(
    val donorsList: ArrayList<String>
/*val names: ArrayList<String>,
val emails: ArrayList<String>,
val numbers: ArrayList<String>*/) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>()
{


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textViewName = itemView.findViewById(R.id.recyclerViewName) as TextView
        val textViewEmail = itemView.findViewById(R.id.recyclerViewEmail) as TextView
        val textViewPhone = itemView.findViewById(R.id.recyclerViewPhone) as TextView
        val btnCall = itemView.findViewById(R.id.recyclerViewCallButton) as ImageButton
        val btnEmail = itemView.findViewById(R.id.recyclerViewEmailButton) as ImageButton


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_layout,parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return donorsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = donorsList[position]
        var name: String? = null
        var email: String? = null
        var phone: String? = null

        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(id)
        val valueEventListener = object: ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot :DataSnapshot) {
                if(dataSnapshot.exists())
                {
                   name = dataSnapshot.child("Name").value.toString()
                    holder.textViewName.text = "Name: $name"
                   email = dataSnapshot.child("Email").value.toString()
                    holder.textViewEmail.text = "Email: $email"
                   phone = dataSnapshot.child("Phone Number").value.toString()
                    holder.textViewPhone.text = "Phone Number: $phone"
                    Log.d("Adapter", "The name is $name the email is $email and the phone is $phone")
                }
                else
                {
                    Log.d("Adapter", "Datasnapshot does not exist")
                }

            }

        }
        reference.addListenerForSingleValueEvent(valueEventListener)


        holder.btnCall.setOnClickListener {
            val num = "tel:$phone"
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.setData(Uri.parse(num))
            holder.itemView.context.startActivity(callIntent)

            val mail = "mailto:$email"
            val mailIntent = Intent(Intent.ACTION_SENDTO)
            mailIntent.setData(Uri.parse(mail))

        }

        holder.btnEmail.setOnClickListener {
            try {


                val emailIntent = Intent(Intent.ACTION_SEND)
               //emailIntent.addCategory(Intent.CATEGORY_APP_EMAIL)
                emailIntent.setType("message/rfc822")
                emailIntent.putExtra("to", email)
                emailIntent.putExtra("subject","Blood Required!")
                holder.itemView.context.startActivity(emailIntent)
            }
            catch (e: ActivityNotFoundException)
            {
                e.printStackTrace()
            }
        }


    }

}