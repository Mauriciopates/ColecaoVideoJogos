package com.example.colecaovideojogos

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavegacao() {
    // 1. O "comboio": controla em que ecrã estamos
    val navController = rememberNavController()

    // 2. Um só ViewModel, partilhado pelos dois ecrãs
    val viewModel: JogosViewModel = viewModel()

    // 3. O "mapa": que ecrãs existem e qual é o primeiro
    NavHost(navController = navController, startDestination = "lista") {

        composable("lista") {
            EcraLista(
                    viewModel = viewModel,
                    aoAdicionar = { navController.navigate("formulario") }
            )
        }

        composable("formulario") {
            EcraFormulario(
                    aoVoltar = { navController.popBackStack() }
            )
        }
    }
}