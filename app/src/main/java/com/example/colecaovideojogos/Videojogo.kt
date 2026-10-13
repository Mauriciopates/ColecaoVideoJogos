package com.example.colecaovideojogos

enum class Estado(val texto: String) {
    POR_JOGAR("Por Jogar"),
    A_JOGAR("A Jogar"),
    CONCLUIDO("Concluído")
}

data class Videojogo(
    val id: Int = 0,
    val nome: String,
    val plataforma: String,
    val genero: String,
    val ano: Int,
    val estado: Estado
)


val jogosExemplo = listOf(
    Videojogo(1, "Zelda: Breath of the Wild", "Switch", "Aventura", 2017, Estado.CONCLUIDO),
    Videojogo(2, "FIFA 23", "PS5", "Desporto", 2022, Estado.A_JOGAR),
    Videojogo(3, "Elden Ring", "PC", "RPG", 2022, Estado.POR_JOGAR),
    Videojogo(4, "Hades", "PC", "Roguelike", 2020, Estado.CONCLUIDO),
    Videojogo(5, "Mario Kart 8", "Switch", "Corridas", 2014, Estado.A_JOGAR),
    Videojogo(6, "God of War", "PS4", "Ação", 2018, Estado.POR_JOGAR),
    Videojogo(7, "The Legend of Zelda: Breath of the Wild", "Switch", "Aventura", 2017, Estado.CONCLUIDO),
    Videojogo(8, "Batman Arkham Knight", "PS4", "Ação", 2015, Estado.CONCLUIDO)
)