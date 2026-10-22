package com.example.colecaovideojogos

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.remember

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
                aoAdicionar = { navController.navigate("formulario/0") },
                aoEditar = { jogo -> navController.navigate("formulario/${jogo.id}") }
            )
        }

        composable(
            route = "formulario/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: 0
            val jogoInicial = remember(id) { if (id > 0) viewModel.obterPorId(id) else null }   // ← MUDOU

            EcraFormulario(
                jogoInicial = jogoInicial,
                aoGuardar = { jogo ->
                    if (jogoInicial == null) {
                        viewModel.adicionar(jogo)
                    } else {
                        viewModel.atualizar(jogo)
                    }
                    navController.popBackStack()
                },
                aoVoltar = { navController.popBackStack() },
                aoEliminar = {                                   // ← NOVO
                    jogoInicial?.let { viewModel.eliminar(it) }
                    navController.popBackStack()
                }
            )
        }
    }
}