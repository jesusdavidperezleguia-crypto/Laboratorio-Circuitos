package com.example.laboratoriocircuitos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TccFondo = Color(0xFF101827)
private val TccPanel = Color(0xFF1C293B)
private val TccNaranja = Color(0xFFFF8733)
private val TccTexto = Color(0xFFF5F7FA)
private val TccSecundario = Color(0xFFB8C4D6)
private val TccVerde = Color(0xFF70D69A)

@Composable
fun PantallaTCC(
    abrirModulo: (String) -> Unit
) {
    var faseSeleccionada by remember { mutableStateOf(0) }

    val fases = listOf(
        "FASE 1 · ANÁLISIS Y NUMERACIÓN",
        "FASE 2 · LÓGICA Y ÁLGEBRA BOOLEANA",
        "FASE 3 · KARNAUGH Y CIRCUITOS COMBINACIONALES",
        "FASE 4 · CIRCUITOS SECUENCIALES Y DOCUMENTACIÓN"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = TccPanel
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "TRABAJO CONTEXTUALIZADO DEL CURSO",
                    color = TccNaranja,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = "SMARTINDUSTRIES CARIBE",
                    color = TccTexto,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )

                Text(
                    text = "Sistema digital de control de acceso y seguridad industrial",
                    color = TccSecundario,
                    fontSize = 14.sp
                )

                HorizontalDivider(color = TccSecundario.copy(alpha = 0.25f))

                Text(
                    text = "OBJETIVO GENERAL",
                    color = TccVerde,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Modelar y desarrollar una solución basada en sistemas digitales para validar el acceso del personal, controlar una puerta industrial, gestionar alarmas y registrar eventos de acceso.",
                    color = TccTexto,
                    fontSize = 14.sp
                )
            }
        }

        Text(
            text = "RUTA DE DESARROLLO DEL TCC",
            color = TccNaranja,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )

        fases.forEachIndexed { indice, fase ->
            val activa = faseSeleccionada == indice

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (activa) TccPanel else TccFondo
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (activa) TccNaranja else TccSecundario.copy(alpha = 0.3f)
                ),
                onClick = {
                    faseSeleccionada = indice
                }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = fase,
                        color = if (activa) TccNaranja else TccTexto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = when (indice) {
                            0 -> "Identificación de entradas, sensores, empleados, códigos y tablas de verdad."
                            1 -> "Funciones de alarma, apertura de puerta y registro de acceso. Simplificación booleana."
                            2 -> "Mapas de Karnaugh, compuertas lógicas, diagramas y simulación combinacional."
                            else -> "Memorias, flip-flops, control de estados, informes técnicos y sustentación."
                        },
                        color = TccSecundario,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TccPanel),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "HERRAMIENTAS DE LA FASE",
                    color = TccVerde,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = when (faseSeleccionada) {
                        0 -> "Comienza identificando las variables de entrada y construyendo el modelo lógico."
                        1 -> "Define las condiciones de seguridad y comprueba sus expresiones booleanas."
                        2 -> "Optimiza las funciones y construye los circuitos combinacionales."
                        else -> "Trabaja con estados, contadores, memoria y documentación final."
                    },
                    color = TccTexto,
                    fontSize = 14.sp
                )

                when (faseSeleccionada) {
                    0 -> {
                        Button(
                            onClick = { abrirModulo("INDUSTRIAL") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ABRIR MODELO INDUSTRIAL")
                        }

                        OutlinedButton(
                            onClick = { abrirModulo("ACTIVIDADES") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CARGAR GUÍA PDF")
                        }
                    }

                    1 -> {
                        Button(
                            onClick = { abrirModulo("ÁLGEBRA BOOLEANA") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ABRIR ÁLGEBRA BOOLEANA")
                        }

                        OutlinedButton(
                            onClick = { abrirModulo("INDUSTRIAL") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ABRIR MODELO INDUSTRIAL")
                        }
                    }

                    2 -> {
                        Button(
                            onClick = { abrirModulo("KARNAUGH") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ABRIR MAPAS DE KARNAUGH")
                        }

                        OutlinedButton(
                            onClick = { abrirModulo("LABORATORIO") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ABRIR LABORATORIO DE CIRCUITOS")
                        }
                    }

                    3 -> {
                        Button(
                            onClick = { abrirModulo("CIRCUITOS SECUENCIALES") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ABRIR CIRCUITOS SECUENCIALES")
                        }

                        OutlinedButton(
                            onClick = { abrirModulo("INFORMES") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ABRIR INFORMES")
                        }
                    }
                }
            }
        }

        Text(
            text = "Las cuatro fases conforman una ruta académica. Las herramientas disponibles se integran progresivamente para desarrollar el caso industrial.",
            color = TccSecundario,
            fontSize = 12.sp
        )
    }
}

