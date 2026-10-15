package com.example.colecaovideojogos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EcraFormulario(aoVoltar: () -> Unit) {
Scaffold { innerPadding ->
        Column(
                modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
        ) {
    Text("Novo jogo", style = MaterialTheme.typography.headlineSmall)
    Text("O formulário vem no próximo bloco.")
    Button(onClick = aoVoltar) {
        Text("Voltar")
    }
}
}
}