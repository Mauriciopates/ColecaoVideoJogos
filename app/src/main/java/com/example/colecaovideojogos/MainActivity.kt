package com.example.colecaovideojogos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// ---------- Cartão de um jogo (com faixa de cor do estado) ----------
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
        // IntrinsicSize.Min: a Row fica com a altura do conteúdo,
        // para a faixa (fillMaxHeight) acompanhar essa altura
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(corDoEstado(jogo.estado))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = jogo.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${jogo.plataforma} · ${jogo.genero} · ${jogo.ano}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Legenda(cor = corDoEstado(jogo.estado), texto = jogo.estado.texto)
            }
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

// ---------- Chips de filtro por estado ----------
@Composable
fun FiltrosEstado(
    selecionado: Estado?,
    total: Int,
    contar: (Estado) -> Int,
    aoSelecionar: (Estado?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selecionado == null,
            onClick = { aoSelecionar(null) },
            label = { Text("Todos ($total)") }
        )
        Estado.entries.forEach { estado ->
            FilterChip(
                selected = selecionado == estado,
                // Tocar no filtro já ativo volta a "Todos"
                onClick = { aoSelecionar(if (selecionado == estado) null else estado) },
                label = { Text("${estado.texto} (${contar(estado)})") },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(corDoEstado(estado))
                    )
                }
            )
        }
    }
}

// ---------- Contador de resultados + menu de ordenação ----------
@Composable
fun BarraResultados(
    quantidade: Int,
    ordenacao: Ordenacao,
    aoMudarOrdenacao: (Ordenacao) -> Unit
) {
    var menuAberto by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (quantidade == 1) "1 jogo" else "$quantidade jogos",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Box: o menu abre "ancorado" ao botão
        Box {
            TextButton(onClick = { menuAberto = true }) {
                Text("⇅ ${ordenacao.texto}")
            }
            DropdownMenu(
                expanded = menuAberto,
                onDismissRequest = { menuAberto = false }
            ) {
                Ordenacao.entries.forEach { opcao ->
                    DropdownMenuItem(
                        text = { Text(opcao.texto) },
                        onClick = {
                            aoMudarOrdenacao(opcao)
                            menuAberto = false
                        },
                        trailingIcon = { if (opcao == ordenacao) Text("✓") }
                    )
                }
            }
        }
    }
}

// ---------- Ecrã da lista ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcraLista(
    viewModel: JogosViewModel,
    aoAdicionar: () -> Unit,
    aoEditar: (Videojogo) -> Unit,
    aoVoltar: () -> Unit
) {
    val jogos = viewModel.jogosFiltrados

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("A minha coleção") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Text("←", fontSize = 24.sp)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = aoAdicionar) {
                Text("+ Adicionar")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.pesquisa,
                onValueChange = { viewModel.alterarPesquisa(it) },
                placeholder = { Text("🔍  Pesquisar por nome") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            FiltrosEstado(
                selecionado = viewModel.filtroEstado,
                total = viewModel.total,
                contar = { viewModel.contarPorEstado(it) },
                aoSelecionar = { viewModel.alterarFiltro(it) }
            )

            BarraResultados(
                quantidade = jogos.size,
                ordenacao = viewModel.ordenacao,
                aoMudarOrdenacao = { viewModel.alterarOrdenacao(it) }
            )

            if (jogos.isEmpty()) {
                Text(
                    text = "Nenhum jogo encontrado.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
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