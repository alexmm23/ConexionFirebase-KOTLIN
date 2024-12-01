package com.example.conexionfirebase

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            database = FirebaseDatabase
                .getInstance("https://conexionfirebase-b90fb-default-rtdb.firebaseio.com/")
                .getReference("users")
                .child("contacts")
            fetchContacts()
        } else {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchContacts() {
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                Log.d("ContactListActivity", "Contact count: ${snapshot.childrenCount}")
                for (contactSnapshot in snapshot.children) {
                    Log.d("ContactListActivity", "Contact key: ${contactSnapshot.value}")
                    val name = contactSnapshot.value.toString()
                    val phone = contactSnapshot.child("phone").getValue(Int::class.java) ?: 0
                    val contact = Contact(name,phone)
                    Log.d("ContactListActivity", "Contact: $contact")
                    if (contact != null) {
                        contactList.add(contact)
                    }
                }
                // Actualizar la lista de contactos
                contactAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ContactListActivity, "Error al cargar contactos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
