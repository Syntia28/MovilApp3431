package com.upn.movilapp3431

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.upn.movilapp3431.ui.theme.MovilApp3431Theme
import com.upn.movilapp3431.viewmodels.ContactListViewModel
import com.upn.movilapp3431.viewmodels.PeliculaListViewModel
import kotlin.getValue

class PeliculaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val CrearPeliculas = LlenadoDeDatosPruebas()
        CrearPeliculas.getPeliculas()
        setContent {
            MovilApp3431Theme {
                val viewModel : PeliculaListViewModel by viewModels()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        ListaElementos(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ListaElementos(viewModel : PeliculaListViewModel) {
    if (viewModel.hasError) {
        Text(text = "Error al cargar peliclulas")
    } else {
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(viewModel.peliculas) { pelicula ->
                    Text(text = "${pelicula.nombreSerie} - ${pelicula.numeroEpisodio}")
                }
            }
        }
    }
}
