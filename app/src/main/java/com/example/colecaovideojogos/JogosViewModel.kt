package com.example.colecaovideojogos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Critérios de ordenação da lista  ← NOVO
enum class Ordenacao(val texto: String) {
    NOME("Nome (A–Z)"),
    RECENTES("Mais recentes"),
    ANO("Ano de lançamento")
}

// AndroidViewModel: igual ao ViewModel, mas recebe a Application (precisamos dela para abrir a BD)
class JogosViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = BaseDados.obter(application).videojogoDao()

    // Lista completa, sempre igual ao que está na base de dados
    private var todos by mutableStateOf<List<Videojogo>>(emptyList())

    var pesquisa by mutableStateOf("")
        private set

    // null = "Todos"  ← NOVO
    var filtroEstado by mutableStateOf<Estado?>(null)
        private set

    var ordenacao by mutableStateOf(Ordenacao.NOME)   // ← NOVO
        private set

    // Pesquisa + filtro + ordenação, aplicados por esta ordem  ← MUDOU
    val jogosFiltrados: List<Videojogo>
        get() {
            val filtrados = todos
                .filter { it.nome.contains(pesquisa, ignoreCase = true) }
                .filter { filtroEstado == null || it.estado == filtroEstado }

            return when (ordenacao) {
                Ordenacao.NOME -> filtrados.sortedBy { it.nome.lowercase() }
                Ordenacao.RECENTES -> filtrados.sortedByDescending { it.id }
                Ordenacao.ANO -> filtrados.sortedByDescending { it.ano }
            }
        }

    init {
        // 1. Na primeira utilização (tabela vazia), insere os jogos de exemplo
        viewModelScope.launch {
            if (dao.contar() == 0) {
                dao.inserirTodos(jogosExemplo)
            }
        }
        // 2. Observa a tabela: sempre que mudar, a lista "todos" é atualizada
        viewModelScope.launch {
            dao.obterTodos().collect { lista ->
                todos = lista
            }
        }
    }

    fun alterarPesquisa(texto: String) {
        pesquisa = texto
    }

    fun alterarFiltro(estado: Estado?) {        // ← NOVO
        filtroEstado = estado
    }

    fun alterarOrdenacao(nova: Ordenacao) {     // ← NOVO
        ordenacao = nova
    }

    fun obterPorId(id: Int): Videojogo? =
        todos.find { it.id == id }

    fun adicionar(jogo: Videojogo) {
        viewModelScope.launch {
            dao.inserir(jogo.copy(id = 0))   // id 0 → o Room gera o próximo id
        }
    }

    fun atualizar(jogo: Videojogo) {
        viewModelScope.launch {
            dao.atualizar(jogo)
        }
    }

    fun eliminar(jogo: Videojogo) {
        viewModelScope.launch {
            dao.eliminar(jogo)
        }
    }

    // ---------- Estatísticas (ecrã inicial) ----------

    val total: Int
        get() = todos.size

    fun contarPorEstado(estado: Estado): Int =
        todos.count { it.estado == estado }

    // Jogos "A Jogar", do mais recente (maior id) para o mais antigo
    val jogosAJogar: List<Videojogo>
        get() = todos
            .filter { it.estado == Estado.A_JOGAR }
            .sortedByDescending { it.id }

    // Quantos jogos há por plataforma, da mais frequente para a menos frequente
    val contagemPorPlataforma: List<Pair<String, Int>>
        get() = todos
            .groupBy { it.plataforma.trim().lowercase() }
            .map { (_, jogos) -> jogos.first().plataforma.trim() to jogos.size }
            .sortedByDescending { it.second }
}