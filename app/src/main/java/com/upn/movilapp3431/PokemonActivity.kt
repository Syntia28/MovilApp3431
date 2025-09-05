package com.upn.movilapp3431

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.upn.movilapp3431.entities.Contact
import com.upn.movilapp3431.entities.Pokemon
import com.upn.movilapp3431.services.ApiService
import com.upn.movilapp3431.ui.theme.MovilApp3431Theme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.plus

class PokemonActivity : ComponentActivity() {



    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        setContent {
            MovilApp3431Theme {

                var pokemons by remember { mutableStateOf(listOf<Pokemon>()) }
                var hasError by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    try {
                        hasError = false
                        isLoading = true
                        val Response = apiService.getPokemon()
                        pokemons  = Response.results
                        Log.i("MAIN_APP", "Pokemon: ${pokemons.size}")
                    } catch (e: Exception) {
                        hasError = true
                        Log.e("MAIN_APP", "Error: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .padding(8.dp)) {
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            if (!hasError) {
                                Column {

                                    LazyColumn {
                                        items(pokemons) { Pokem->
                                            val context = LocalContext.current
                                            Card(

                                                modifier = Modifier.fillMaxWidth()

                                                    .padding(vertical = 10.dp, horizontal = 4.dp)
                                                    .clickable {
                                                        Toast.makeText(
                                                            context,
                                                            "Seleccionó: ${Pokem.name}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                            ) {
                                                Column (modifier = Modifier.background(Color(0xFF6200EE))){
                                                    Text(
                                                        text = Pokem.name,
                                                        modifier = Modifier.padding(4.dp)
                                                    )
                                                    Text(
                                                        text = Pokem.url,
                                                        modifier = Modifier.padding(4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(text = "Ocurrió un error al cargar los datos")
                            }
                        }
                    }
                }
            }
        }
    }
}

