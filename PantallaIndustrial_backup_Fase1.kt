package com.example.laboratoriocircuitos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos

private val IndustrialFondo = Color(0xFF101827)
private val IndustrialPanel = Color(0xFF1C293B)
private val IndustrialNaranja = Color(0xFFFF8733)
private val IndustrialVerde = Color(0xFF70D69A)
private val IndustrialTexto = Color(0xFFF5F7FA)
private val IndustrialSecundario = Color(0xFFB8C4D6)

@Composable
fun PantallaIndustrial() {
    var tarjetaAutorizada by remember { mutableStateOf(true) }
    var pinCorrecto by remember { mutableStateOf(true) }
    var puertaCerrada by remember { mutableStateOf(true) }
    var emergenciaActivada by remember { mutableStateOf(false) }

    val formula = "S = A*B*C*!D"
    val circuito = remember {
        GeneradorCircuitos.generar(formula)
    }

    val accesoPermitido =
        tarjetaAutorizada &&
        pinCorrecto &&
        puertaCerrada &&
        !emergenciaActivada

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "SISTEMA INDUSTRIAL",
            color = IndustrialNaranja,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "SIMULADOR DE CONTROL DE ACCESO",
            color = IndustrialTexto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Modifica las entradas para observar cómo responde el sistema.",
            color = IndustrialSecundario,
            fontSize = 13.sp
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = IndustrialPanel
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text(
                    text = "ENTRADAS DEL SISTEMA",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                EntradaIndustrial(
                    titulo = "A - Tarjeta autorizada",
                    descripcion = "La tarjeta presentada está registrada.",
                    valor = tarjetaAutorizada,
                    cambiar = { tarjetaAutorizada = it }
                )

                HorizontalDivider(color = IndustrialSecundario.copy(alpha = 0.25f))

                EntradaIndustrial(
                    titulo = "B - PIN correcto",
                    descripcion = "El código ingresado es válido.",
                    valor = pinCorrecto,
                    cambiar = { pinCorrecto = it }
                )

                HorizontalDivider(color = IndustrialSecundario.copy(alpha = 0.25f))

                EntradaIndustrial(
                    titulo = "C - Puerta cerrada",
                    descripcion = "El sensor indica que la puerta está cerrada.",
                    valor = puertaCerrada,
                    cambiar = { puertaCerrada = it }
                )

                HorizontalDivider(color = IndustrialSecundario.copy(alpha = 0.25f))

                EntradaIndustrial(
                    titulo = "D - Parada de emergencia",
                    descripcion = "Activa esta entrada para bloquear el acceso.",
                    valor = emergenciaActivada,
                    cambiar = { emergenciaActivada = it }
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = IndustrialPanel
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "RESULTADO DE LA SIMULACIÓN",
                    color = IndustrialTexto,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (accesoPermitido) "ACCESO PERMITIDO" else "ACCESO BLOQUEADO",
                    color = if (accesoPermitido) IndustrialVerde else IndustrialNaranja,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (accesoPermitido) {
                        "Todas las condiciones se cumplen."
                    } else {
                        "Una o más condiciones impiden autorizar el acceso."
                    },
                    color = IndustrialSecundario,
                    fontSize = 13.sp
                )

                Text(
                    text = "Salida S = ${if (accesoPermitido) 1 else 0}",
                    color = IndustrialTexto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = IndustrialPanel
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "FUNCIÓN BOOLEANA",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formula,
                    color = IndustrialTexto,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "S = A · B · C · ¬D",
                    color = IndustrialSecundario,
                    fontSize = 15.sp
                )

                Text(
                    text = "A: tarjeta | B: PIN | C: puerta cerrada | D: emergencia",
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = IndustrialPanel
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "CIRCUITO LÓGICO",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                CircuitoVisual(raiz = circuito.raiz)
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = IndustrialPanel
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ESTADO DE LAS ENTRADAS",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                FilaEstadoIndustrial("A - Tarjeta", tarjetaAutorizada)
                FilaEstadoIndustrial("B - PIN", pinCorrecto)
                FilaEstadoIndustrial("C - Puerta", puertaCerrada)
                FilaEstadoIndustrial("D - Emergencia", emergenciaActivada)
                HorizontalDivider(color = IndustrialSecundario.copy(alpha = 0.25f))
                FilaEstadoIndustrial("S - Salida", accesoPermitido)
            }
        }
    }
}

@Composable
private fun EntradaIndustrial(
    titulo: String,
    descripcion: String,
    valor: Boolean,
    cambiar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                color = IndustrialTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = descripcion,
                color = IndustrialSecundario,
                fontSize = 12.sp
            )
            Text(
                text = if (valor) "Estado: 1" else "Estado: 0",
                color = if (valor) IndustrialVerde else IndustrialNaranja,
                fontSize = 12.sp
            )
        }

        Switch(
            checked = valor,
            onCheckedChange = cambiar,
            colors = SwitchDefaults.colors(
                checkedThumbColor = IndustrialVerde,
                checkedTrackColor = IndustrialVerde.copy(alpha = 0.35f),
                uncheckedThumbColor = IndustrialTexto,
                uncheckedTrackColor = IndustrialSecundario.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun FilaEstadoIndustrial(
    nombre: String,
    valor: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nombre,
            color = IndustrialTexto,
            fontSize = 13.sp
        )
        Text(
            text = if (valor) "1 - ACTIVO" else "0 - INACTIVO",
            color = if (valor) IndustrialVerde else IndustrialNaranja,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
