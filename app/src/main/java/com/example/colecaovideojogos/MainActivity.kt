package com.example.colecaovideojogos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.colecaovideojogos.ui.theme.ColecaoVideoJogosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColecaoVideoJogosTheme {
                AppNavegacao()
            }
        }
    }
}

// ---------- Cartão de um jogo ----------
@Composable
fun CartaoJogo(
    jogo: Videojogo,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = aoClicar,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = jogo.nome,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${jogo.plataforma} · ${jogo.genero} · ${jogo.ano}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = jogo.estado.texto,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ---------- Lista de jogos ----------
@Composable
fun ListaJogos(
    jogos: List<Videojogo>,
    aoClicar: (Videojogo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        // bottom = 88.dp: espaço para o botão "+ Adicionar" não tapar o último cartão
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(jogos, key = { it.id }) { jogo ->
            CartaoJogo(jogo = jogo, aoClicar = { aoClicar(jogo) })
        }
    }
}

// ---------- Ecrã principal ----------
@Composable
fun EcraLista(
    viewModel: JogosViewModel,
    aoAdicionar: () -> Unit,
    aoEditar: (Videojogo) -> Unit
) {
    val jogos = viewModel.jogosFiltrados

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = aoAdicionar) {
                Text("+ Adicionar")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "A minha coleção",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            OutlinedTextField(
                value = viewModel.pesquisa,
                onValueChange = { viewModel.alterarPesquisa(it) },
                label = { Text("Pesquisar por nome") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (jogos.isEmpty()) {
                Text("Nenhum jogo encontrado.", modifier = Modifier.padding(16.dp))
            } else {
                ListaJogos(jogos = jogos, aoClicar = aoEditar)
            }
        }
    }
}

// ---------- Preview ----------
@Preview(showBackground = true)
@Composable
fun CartaoJogoPreview() {
    ColecaoVideoJogosTheme {
        CartaoJogo(
            jogo = Videojogo(1, "Zelda", "Switch", "Aventura", 2017, Estado.CONCLUIDO),
            aoClicar = {}
        )
    }
}