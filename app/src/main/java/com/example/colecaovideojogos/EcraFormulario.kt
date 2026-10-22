package com.example.colecaovideojogos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun EcraFormulario(
    jogoInicial: Videojogo? = null,          // null = novo jogo; preenchido = editar
    aoGuardar: (Videojogo) -> Unit,
    aoVoltar: () -> Unit,
    aoEliminar: () -> Unit = {}              // ← NOVO
) {
    // 1. Estado de cada campo, já com os valores do jogo (se estivermos a editar)
    var nome by rememberSaveable { mutableStateOf(jogoInicial?.nome ?: "") }
    var plataforma by rememberSaveable { mutableStateOf(jogoInicial?.plataforma ?: "") }
    var genero by rememberSaveable { mutableStateOf(jogoInicial?.genero ?: "") }
    var ano by rememberSaveable { mutableStateOf(jogoInicial?.ano?.toString() ?: "") }
    var estado by rememberSaveable { mutableStateOf(jogoInicial?.estado ?: Estado.POR_JOGAR) }
    var tentouGuardar by rememberSaveable { mutableStateOf(false) }
    var mostrarConfirmacao by rememberSaveable { mutableStateOf(false) }   // ← NOVO

    // 2. Validação: cada campo tem uma mensagem de erro, ou null se estiver OK
    val anoMaximo = Calendar.getInstance().get(Calendar.YEAR) + 1
    val erroNome = if (nome.isBlank()) "O nome é obrigatório" else null
    val erroPlataforma = if (plataforma.isBlank()) "A plataforma é obrigatória" else null
    val erroGenero = if (genero.isBlank()) "O género é obrigatório" else null
    val erroAno = when {
        ano.isBlank() -> "O ano é obrigatório"
        ano.toIntOrNull() == null -> "Introduza apenas números"
        ano.toInt() !in 1950..anoMaximo -> "O ano deve estar entre 1950 e $anoMaximo"
        else -> null
    }
    val formularioValido =
        listOf(erroNome, erroPlataforma, erroGenero, erroAno).all { it == null }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (jogoInicial == null) "Novo jogo" else "Editar jogo",
                style = MaterialTheme.typography.headlineSmall
            )

            // 3. Campos de texto (só mostram erros depois de tentar guardar)
            CampoTexto("Nome", nome, { nome = it }, if (tentouGuardar) erroNome else null)
            CampoTexto("Plataforma", plataforma, { plataforma = it }, if (tentouGuardar) erroPlataforma else null)
            CampoTexto("Género", genero, { genero = it }, if (tentouGuardar) erroGenero else null)
            CampoTexto(
                rotulo = "Ano de lançamento",
                valor = ano,
                aoMudar = { ano = it },
                erro = if (tentouGuardar) erroAno else null,
                teclado = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 4. Estado: uma opção de cada vez (RadioButton)
            Text("Estado", style = MaterialTheme.typography.titleMedium)
            Estado.entries.forEach { opcao ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = estado == opcao,
                            onClick = { estado = opcao },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(selected = estado == opcao, onClick = null)
                    Text(opcao.texto, modifier = Modifier.padding(start = 8.dp))
                }
            }

            // 5. Botões
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = aoVoltar) {
                    Text("Cancelar")
                }
                Button(onClick = {
                    tentouGuardar = true
                    if (formularioValido) {
                        aoGuardar(
                            Videojogo(
                                id = jogoInicial?.id ?: 0,
                                nome = nome.trim(),
                                plataforma = plataforma.trim(),
                                genero = genero.trim(),
                                ano = ano.toInt(),
                                estado = estado
                            )
                        )
                    }
                }) {
                    Text("Guardar")
                }
            }

            // 6. Eliminar: só aparece ao editar  ← NOVO
            if (jogoInicial != null) {
                OutlinedButton(
                    onClick = { mostrarConfirmacao = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar jogo")
                }
            }
        }

        // 7. Diálogo de confirmação  ← NOVO
        if (mostrarConfirmacao) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacao = false },
                title = { Text("Eliminar jogo") },
                text = {
                    Text("Tem a certeza que quer eliminar \"${jogoInicial?.nome}\"? Esta ação não pode ser anulada.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarConfirmacao = false
                        aoEliminar()
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacao = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// Componente reutilizável: um campo de texto com mensagem de erro
@Composable
fun CampoTexto(
    rotulo: String,
    valor: String,
    aoMudar: (String) -> Unit,
    erro: String?,
    teclado: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        label = { Text(rotulo) },
        singleLine = true,
        isError = erro != null,
        supportingText = { if (erro != null) Text(erro) },
        keyboardOptions = teclado,
        modifier = Modifier.fillMaxWidth()
    )
}