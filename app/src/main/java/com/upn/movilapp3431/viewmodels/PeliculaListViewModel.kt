package com.upn.movilapp3431.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.upn.movilapp3431.entities.Contact
import com.upn.movilapp3431.entities.Peliculas

class PeliculaListViewModel :  ViewModel() {
    val  peliculas  = mutableStateListOf<Peliculas>() //contine una lista de peliciulas
    var isLoading by mutableStateOf(false)
    var hasError by mutableStateOf(false)

    private val database = Firebase.database
    private val contactsRef = database.getReference("peliculas")

    init {
        loadContacts()
    }

    private fun loadContacts() {
        isLoading = true

        contactsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempPeliculas = mutableListOf<Peliculas>()
                for (item in dataSnapshot.children) {
                    Log.d("MAIN_APP", "Value is: $item")
                    val pelicula = item.getValue(Peliculas::class.java)
                    tempPeliculas.add(pelicula!!)
                }

                peliculas.addAll(tempPeliculas)
                isLoading = false

                Log.d("MAIN_APP", "Value is: ${peliculas.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MAIN_APP", "Failed to read value.", error.toException())
                isLoading = false
                hasError = true
            }
        })

    }
}
