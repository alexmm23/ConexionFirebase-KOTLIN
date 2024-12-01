package com.example.conexionfirebase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
data class Contact(val name: String, val phone: Int)

class ContactAdapter(private val contactList: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.tvName.text = contact.name
        holder.tvPhone.text = contact.phone.toString()
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}
