package com.example.colecaovideojogos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.colecaovideojogos.ui.theme.ColecaoVideoJogosTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColecaoVideoJogosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EcraLista(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CartaoJogo(jogo: Videojogo, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = jogo.nome, style = MaterialTheme.typography.titleLarge)
            Text(text = "${jogo.plataforma} • ${jogo.genero} • ${jogo.ano}")
            Text(text = jogo.estado.texto)
        }
    }
}

@Composable
fun ListaJogos(jogos: List<Videojogo>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(jogos) { jogo ->
            CartaoJogo(jogo = jogo)
        }
    }
}
@Composable
fun EcraLista(
    modifier: Modifier = Modifier,
    viewModel: JogosViewModel = viewModel()
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = viewModel.pesquisa,
            onValueChange = { viewModel.alterarPesquisa(it) },
            label = { Text("Pesquisar por nome") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        val jogos = viewModel.jogosFiltrados
        if (jogos.isEmpty()) {
            Text("Nenhum jogo encontrado.", modifier = Modifier.padding(16.dp))
        } else {
            ListaJogos(jogos = jogos)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartaoJogoPreview() {
    ColecaoVideoJogosTheme {
        CartaoJogo(
            jogo = Videojogo(1, "Zelda", "Switch", "Aventura", 2017, Estado.CONCLUIDO)
        )
    }
}
