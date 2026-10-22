package com.example.colecaovideojogos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavegacao() {
    // 1. O "comboio": controla em que ecrã estamos
    val navController = rememberNavController()

    // 2. Um só ViewModel, partilhado por todos os ecrãs
    val viewModel: JogosViewModel = viewModel()

    // 3. O "mapa": que ecrãs existem e qual é o primeiro
    NavHost(navController = navController, startDestination = "inicio") {   // ← MUDOU

        // Ecrã inicial com estatísticas  ← NOVO
        composable("inicio") {
            EcraInicial(
                viewModel = viewModel,
                aoVerColecao = { navController.navigate("lista") },
                aoAdicionar = { navController.navigate("formulario/0") },
                aoAbrirJogo = { jogo -> navController.navigate("formulario/${jogo.id}") }
            )
        }

        composable("lista") {
            EcraLista(
                viewModel = viewModel,
                aoAdicionar = { navController.navigate("formulario/0") },
                aoEditar = { jogo -> navController.navigate("formulario/${jogo.id}") },
                aoVoltar = { navController.popBackStack() }
            )
        }

        composable(
            route = "formulario/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: 0
            val jogoInicial = remember(id) { if (id > 0) viewModel.obterPorId(id) else null }

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
                aoEliminar = {
                    jogoInicial?.let { viewModel.eliminar(it) }
                    navController.popBackStack()
                }
            )
        }
    }
}