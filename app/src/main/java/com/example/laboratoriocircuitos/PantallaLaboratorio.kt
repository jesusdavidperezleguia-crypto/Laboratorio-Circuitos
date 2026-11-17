package com.example.laboratoriocircuitos

import androidx.compose.foundation.background
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
import com.example.laboratoriocircuitos.logic.*

private val FondoLaboratorio = Color(0xFF101827)
private val PanelLaboratorio = Color(0xFF1C293B)
private val NaranjaLaboratorio = Color(0xFFFF8733)
private val VerdeLaboratorio = Color(0xFF70D69A)
private val TextoLaboratorio = Color(0xFFF5F7FA)

@Composable
fun PantallaLaboratorioBooleano(formulaInicial: String? = null) {

    var formula by remember {
        mutableStateOf("Z = (A+B)Â·Â¬C")
    }

    var circuito by remember {
        mutableStateOf<CircuitoGenerado?>(null)
    }

    var tabla by remember {
        mutableStateOf<TablaVerdadResultado?>(null)
    }

    var valores by remember {
        mutableStateOf<Map<String, Boolean>>(emptyMap())
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(formulaInicial) {
        if (!formulaInicial.isNullOrBlank()) {
            formula = formulaInicial
            circuito = null
            tabla = null
            valores = emptyMap()
            error = null
        }
    }

    val resultado = remember(circuito, valores) {
        try {
            if (circuito != null) {
                SimuladorCircuitos.simular(circuito!!, valores)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                PanelLaboratorio,
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "LABORATORIO BOOLEANO",
            color = NaranjaLaboratorio,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Escribe una expresiÃ³n para generar su circuito, tabla de verdad y simulaciÃ³n.",
            color = TextoLaboratorio,
            fontSize = 13.sp
        )

        OutlinedTextField(
            value = formula,
            onValueChange = {
                formula = it
                error = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("ExpresiÃ³n booleana")
            },
            placeholder = {
                Text("Ejemplo: Z = A+B")
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextoLaboratorio,
                unfocusedTextColor = TextoLaboratorio,
                focusedBorderColor = NaranjaLaboratorio,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = NaranjaLaboratorio,
                unfocusedLabelColor = Color.LightGray
            )
        )

        Button(
            onClick = {
                try {
                    val nuevoCircuito =
                        GeneradorCircuitos.generar(formula)

                    val nuevaTabla =
                        GeneradorTablaVerdad.generar(formula)

                    circuito = nuevoCircuito
                    tabla = nuevaTabla

                    valores = nuevoCircuito.variables.associateWith {
                        false
                    }

                    error = null

                } catch (e: Exception) {
                    circuito = null
                    tabla = null
                    valores = emptyMap()

                    error = e.message
                        ?: "No se pudo procesar la expresiÃ³n."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = NaranjaLaboratorio,
                contentColor = Color.Black
            )
        ) {
            Text(
                "GENERAR CIRCUITO, TABLA Y SIMULAR",
                fontWeight = FontWeight.Bold
            )
        }

        if (error != null) {
            Text(
                text = error ?: "",
                color = Color(0xFFFF7777),
                fontSize = 13.sp
            )
        }

        if (circuito != null && tabla != null) {

            HorizontalDivider(color = Color.Gray)

            Text(
                text = "ESTRUCTURA DEL CIRCUITO",
                color = VerdeLaboratorio,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "ExpresiÃ³n: ${circuito!!.expresion}",
                color = TextoLaboratorio
            )

            Text(
                text = "Variables: ${circuito!!.variables.joinToString(", ")}",
                color = TextoLaboratorio
            )

            Text(
                text = "Compuertas detectadas: ${circuito!!.cantidadCompuertas}",
                color = TextoLaboratorio
            )

            // DIAGRAMA GENERADO AUTOMÃTICAMENTE
            CircuitoVisual(
                raiz = circuito!!.raiz
            )

            HorizontalDivider(color = Color.Gray)

            Text(
                text = "SIMULADOR DE ENTRADAS",
                color = VerdeLaboratorio,
                fontWeight = FontWeight.Bold
            )

            circuito!!.variables.forEach { variable ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Entrada $variable: ${
                            if (valores[variable] == true) "1" else "0"
                        }",
                        color = TextoLaboratorio,
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = valores[variable] == true,
                        onCheckedChange = { nuevoValor ->
                            valores = valores.toMutableMap().apply {
                                put(variable, nuevoValor)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = VerdeLaboratorio,
                            checkedTrackColor = VerdeLaboratorio.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = FondoLaboratorio
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = "RESULTADO DE LA SIMULACIÃ“N",
                        color = NaranjaLaboratorio,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = resultado?.mensaje ?: "Esperando entradas...",
                        color = if (resultado?.resultadoSalida == true) {
                            VerdeLaboratorio
                        } else {
                            TextoLaboratorio
                        },
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Salida: ${
                            if (resultado?.resultadoSalida == true) "1" else "0"
                        }",
                        color = TextoLaboratorio,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            HorizontalDivider(color = Color.Gray)

            Text(
                text = "TABLA DE VERDAD",
                color = VerdeLaboratorio,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoLaboratorio)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabla!!.variables.forEach { variable ->
                    Text(
                        text = variable,
                        color = NaranjaLaboratorio,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Z",
                    color = VerdeLaboratorio,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            tabla!!.filas.take(32).forEach { fila ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabla!!.variables.forEach { variable ->
                        Text(
                            text = if (fila.entradas[variable] == true) "1" else "0",
                            color = TextoLaboratorio,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = if (fila.salida) "1" else "0",
                        color = VerdeLaboratorio,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (tabla!!.filas.size > 32) {
                Text(
                    text = "Se muestran las primeras 32 filas de ${tabla!!.filas.size}.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
