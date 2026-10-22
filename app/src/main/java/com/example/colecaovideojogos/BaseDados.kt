package com.example.colecaovideojogos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @Database: junta as tabelas (entities) e os DAOs numa base de dados SQLite
@Database(entities = [Videojogo::class], version = 1, exportSchema = false)
abstract class BaseDados : RoomDatabase() {

    // O Room gera a implementação deste DAO (via KSP)
    abstract fun videojogoDao(): VideojogoDao

    companion object {
        // Única instância da base de dados em toda a app (padrão Singleton)
        @Volatile
        private var instancia: BaseDados? = null

        fun obter(context: Context): BaseDados =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseDados::class.java,
                    "colecao_videojogos.db"          // nome do ficheiro no telemóvel
                ).build().also { instancia = it }
            }
    }
}