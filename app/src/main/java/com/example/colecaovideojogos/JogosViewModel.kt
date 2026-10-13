package com.example.colecaovideojogos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class JogosViewModel : ViewModel() {

    private val _jogos = mutableStateListOf<Videojogo>().apply { addAll(jogosExemplo) }

    var pesquisa by mutableStateOf("")
        private set

    val jogosFiltrados: List<Videojogo>
        get() = _jogos.filter { it.nome.contains(pesquisa, ignoreCase = true) }

    fun alterarPesquisa(texto: String) {
        pesquisa = texto
    }
}