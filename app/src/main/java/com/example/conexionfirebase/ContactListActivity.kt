package com.example.conexionfirebase

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ContactListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private val contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        recyclerView = findViewById(R.id.rvContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        contactAdapter = ContactAdapter(contactList)
        recyclerView.adapter = contactAdapter

        database = FirebaseDatabase
                .getInstance("https://conexionfirebase-b90fb-default-rtdb.firebaseio.com/").reference.child("users")
        fetchContacts()
    }

    private fun fetchContacts() {
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                Log.d("ContactListActivity", "User count: ${snapshot.childrenCount}")
                for (userSnapshot in snapshot.children) {
                    val email = userSnapshot.child("email").getValue(String::class.java)
                    if (email != null) {
                        contactList.add(
                            Contact(
                                email = email,
                                name = email.substring(0, email.indexOf('@')),
                                phone = userSnapshot.child("phone").getValue(Int::class.java) ?: 0
                            )
                        )
                    }
                }
                contactAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ContactListActivity, "Error loading users: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}