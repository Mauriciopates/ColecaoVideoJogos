package com.example.colecaovideojogos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.colecaovideojogos.ui.theme.ColecaoVideoJogosTheme

// Cor associada a cada estado (usada na barra e na legenda)
fun corDoEstado(estado: Estado): Color = when (estado) {
    Estado.POR_JOGAR -> Color(0xFF9E9AE8)   // lilás
    Estado.A_JOGAR -> Color(0xFFEF9F27)     // âmbar
    Estado.CONCLUIDO -> Color(0xFF3DBE8B)   // verde
}

@Composable
fun EcraInicial(
    viewModel: JogosViewModel,
    aoVerColecao: () -> Unit,
    aoAdicionar: () -> Unit,
    aoAbrirJogo: (Videojogo) -> Unit
) {
    val total = viewModel.total
    val aJogar = viewModel.jogosAJogar
    val contagens = Estado.entries.associateWith { viewModel.contarPorEstado(it) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Cabeçalho
            Column {
                Text(
                    text = "A minha coleção",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (total == 1) "1 jogo registado" else "$total jogos registados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 2. Destaque: o jogo que está a ser jogado
            CartaoAJogarAgora(
                jogo = aJogar.firstOrNull(),
                outros = aJogar.drop(1),
                aoClicar = aoAbrirJogo
            )

            // 3. Barra com a proporção de cada estado
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TituloSecao("Estado da coleção")
                BarraEstados(contagens)
            }

            // 4. Contagem por plataforma
            if (viewModel.contagemPorPlataforma.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TituloSecao("Plataformas")
                    ChipsPlataformas(viewModel.contagemPorPlataforma)
                }
            }

            // 5. Empurra os botões para o fundo do ecrã
            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = aoVerColecao, modifier = Modifier.fillMaxWidth()) {
                Text("Ver coleção")
            }
            OutlinedButton(onClick = aoAdicionar, modifier = Modifier.fillMaxWidth()) {
                Text("+ Adicionar jogo")
            }
        }
    }
}

@Composable
fun TituloSecao(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// Cartão de destaque "A JOGAR AGORA"
@Composable
fun CartaoAJogarAgora(
    jogo: Videojogo?,
    outros: List<Videojogo>,
    aoClicar: (Videojogo) -> Unit
) {
    val cores = MaterialTheme.colorScheme

    Card(
        onClick = { jogo?.let(aoClicar) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cores.primary,
            contentColor = cores.onPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("A JOGAR AGORA", style = MaterialTheme.typography.labelMedium)

            if (jogo == null) {
                // Estado vazio: nenhum jogo marcado como "A Jogar"
                Text(
                    text = "Nenhum jogo em curso",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Marca um jogo como \"A Jogar\" para o veres aqui.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = jogo.nome,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${jogo.plataforma} · ${jogo.genero} · ${jogo.ano}",
                    style = MaterialTheme.typography.bodyMedium
                )

                // "Pílula" com os outros jogos em curso
                if (outros.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${outros.size} a jogar · " + outros.take(2).joinToString { it.nome },
                        style = MaterialTheme.typography.labelMedium,
                        color = cores.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(cores.onPrimary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// Barra horizontal dividida pela quantidade de jogos em cada estado + legenda
@Composable
fun BarraEstados(contagens: Map<Estado, Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            contagens.filter { it.value > 0 }.forEach { (estado, quantidade) ->
                Box(
                    modifier = Modifier
                        .weight(quantidade.toFloat())
                        .fillMaxHeight()
                        .background(corDoEstado(estado))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            contagens.forEach { (estado, quantidade) ->
                val nome = estado.texto.lowercase()
                // "concluído" vai para o plural quando não é 1 (0, 2, 3... → "concluídos")
                val nomeFinal = if (estado == Estado.CONCLUIDO && quantidade != 1) "${nome}s" else nome
                Legenda(cor = corDoEstado(estado), texto = "$quantidade $nomeFinal")
            }
        }
    }
}

@Composable
fun Legenda(cor: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(cor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium)
    }
}

// "Chips" com a contagem por plataforma; passam para a linha seguinte se não couberem
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsPlataformas(plataformas: List<Pair<String, Int>>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        plataformas.take(6).forEach { (nome, quantidade) ->
            SuggestionChip(
                onClick = {},
                label = { Text("$nome · $quantidade") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EcraInicialPartesPreview() {
    ColecaoVideoJogosTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CartaoAJogarAgora(
                jogo = Videojogo(2, "FIFA 23", "PS5", "Desporto", 2022, Estado.A_JOGAR),
                outros = listOf(Videojogo(5, "Mario Kart 8", "Switch", "Corridas", 2014, Estado.A_JOGAR)),
                aoClicar = {}
            )
            BarraEstados(
                mapOf(Estado.POR_JOGAR to 3, Estado.A_JOGAR to 2, Estado.CONCLUIDO to 3)
            )
            ChipsPlataformas(listOf("PC" to 3, "PS4" to 2, "Switch" to 2, "PS5" to 1))
        }
    }
}