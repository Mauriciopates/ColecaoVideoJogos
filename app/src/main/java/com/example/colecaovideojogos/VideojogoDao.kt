package com.example.colecaovideojogos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// @Dao (Data Access Object): todas as operações sobre a tabela "videojogos"
@Dao
interface VideojogoDao {

    // Lista todos os jogos. Flow = a lista atualiza-se SOZINHA quando a tabela muda
    @Query("SELECT * FROM videojogos ORDER BY id")
    fun obterTodos(): Flow<List<Videojogo>>

    // Quantos jogos existem (para saber se é a primeira utilização)
    @Query("SELECT COUNT(*) FROM videojogos")
    suspend fun contar(): Int

    @Insert
    suspend fun inserir(jogo: Videojogo)

    @Insert
    suspend fun inserirTodos(jogos: List<Videojogo>)

    @Update
    suspend fun atualizar(jogo: Videojogo)

    @Delete
    suspend fun eliminar(jogo: Videojogo)
}