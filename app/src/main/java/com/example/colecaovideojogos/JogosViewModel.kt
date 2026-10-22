package com.example.colecaovideojogos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// AndroidViewModel: igual ao ViewModel, mas recebe a Application (precisamos dela para abrir a BD)
class JogosViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = BaseDados.obter(application).videojogoDao()

    // Lista completa, sempre igual ao que está na base de dados
    private var todos by mutableStateOf<List<Videojogo>>(emptyList())

    var pesquisa by mutableStateOf("")
        private set

    val jogosFiltrados: List<Videojogo>
        get() = todos.filter { it.nome.contains(pesquisa, ignoreCase = true) }

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
}