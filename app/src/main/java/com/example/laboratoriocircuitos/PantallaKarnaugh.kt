package com.example.laboratoriocircuitos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.laboratoriocircuitos.logic.GeneradorKarnaugh
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.SimuladorCircuitos
import com.example.laboratoriocircuitos.logic.ComparadorCircuitos
import com.example.laboratoriocircuitos.logic.GeneradorTablaVerdad
import com.example.laboratoriocircuitos.logic.ResultadoKarnaugh

private val KarnaughFondo = Color(0xFF101827)
private val KarnaughPanel = Color(0xFF1C293B)
private val KarnaughNaranja = Color(0xFFFF8733)
private val KarnaughVerde = Color(0xFF70D69A)
private val KarnaughTexto = Color(0xFFFFFFFF)

private fun verificarEquivalenciaKarnaugh(
    original: String,
    simplificada: String
): Boolean {
    val tablaOriginal = GeneradorTablaVerdad.generar(original)
    val tablaSimplificada = GeneradorTablaVerdad.generar(simplificada)

    if (tablaOriginal.variables != tablaSimplificada.variables) {
        return false
    }

    if (tablaOriginal.filas.size != tablaSimplificada.filas.size) {
        return false
    }

    return tablaOriginal.filas.zip(tablaSimplificada.filas).all {
            (filaOriginal, filaSimplificada) ->
        filaOriginal.salida == filaSimplificada.salida
    }
}

@Composable
fun PantallaKarnaugh(
    onResultadosActualizados: (DatosInformeTCC) -> Unit = {}
) {

    var formula by remember {
        mutableStateOf("F = A*B + A*C")
    }

    var resultado by remember {
        mutableStateOf<ResultadoKarnaugh?>(null)
    }

    var equivalencia by remember {
        mutableStateOf<Boolean?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var entradasSimulacion by remember {
        mutableStateOf<Map<String, Boolean>>(emptyMap())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(KarnaughFondo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "MAPAS DE KARNAUGH",
            color = KarnaughNaranja,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "SimplificaciÃƒÆ’Ã‚Â³n de funciones booleanas de 2 a 6 variables.",
            color = KarnaughTexto
        )

        Text(
            "FUNCIONES DEL PROYECTO SMARTINDUSTRIES",
            color = KarnaughNaranja,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    formula = "AL = D+!E"
                    resultado = null
                    equivalencia = null
                    error = null
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KarnaughVerde,
                    contentColor = Color.Black
                )
            ) {
                Text("ALARMA")
            }

            Button(
                onClick = {
                    formula = "R = A*B"
                    resultado = null
                    equivalencia = null
                    error = null
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KarnaughVerde,
                    contentColor = Color.Black
                )
            ) {
                Text("REGISTRO")
            }
        }

        Button(
            onClick = {
                formula = "AP = A*B*C*!D*E*F"
                resultado = null
                equivalencia = null
                error = null
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = KarnaughNaranja,
                contentColor = Color.Black
            )
        ) {
            Text("APERTURA INDUSTRIAL (6 VARIABLES)")
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF29384C)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Variables industriales: A tarjeta autorizada, B PIN correcto, C puerta cerrada, D emergencia, E paso ÃƒÆ’Ã‚Â³ptico despejado y F proximidad detectada.",
                color = KarnaughTexto,
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }

        OutlinedTextField(
            value = formula,
            onValueChange = {
                formula = it
                resultado = null
                equivalencia = null
                error = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("FunciÃƒÆ’Ã‚Â³n booleana") },
            placeholder = { Text("AP = A*B*C*!D*E*F") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = KarnaughTexto,
                unfocusedTextColor = KarnaughTexto,
                focusedBorderColor = KarnaughNaranja,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = KarnaughNaranja,
                unfocusedLabelColor = Color.LightGray
            )
        )

        Button(
            onClick = {
                try {
                    val nuevoResultado = GeneradorKarnaugh.generar(formula)

                    val esEquivalente = verificarEquivalenciaKarnaugh(
                        formula,
                        nuevoResultado.formulaSimplificada
                    )

                    resultado = nuevoResultado
                    equivalencia = esEquivalente
                    entradasSimulacion = nuevoResultado.variables.associateWith {
                        false
                    }
                    error = null
                } catch (e: Exception) {
                    resultado = null
                    equivalencia = null
                    error = e.message ?: "No se pudo generar el mapa."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = KarnaughNaranja,
                contentColor = Color.Black
            )
        ) {
            Text(
                "GENERAR MAPA Y VERIFICAR",
                fontWeight = FontWeight.Bold
            )
        }

        error?.let {
            Text(
                it,
                color = Color(0xFFFF7777),
                fontWeight = FontWeight.Bold
            )
        }

        resultado?.let { datos ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KarnaughPanel
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "MAPA DE KARNAUGH",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Variables: ${datos.variables.joinToString(", ")}",
                        color = KarnaughTexto
                    )

                    Text(
                        "Combinaciones: ${1 shl datos.variables.size}",
                        color = KarnaughVerde,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(0.7f))

                        datos.columnasGray.forEach { columna ->
                            Text(
                                columna,
                                modifier = Modifier.weight(1f),
                                color = KarnaughNaranja,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    datos.mapa.forEachIndexed { indiceFila, fila ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                datos.filasGray[indiceFila],
                                modifier = Modifier.weight(0.7f),
                                color = KarnaughNaranja,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )

                            fila.forEach { valor ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .border(
                                            1.dp,
                                            Color.Gray,
                                            RoundedCornerShape(3.dp)
                                        )
                                        .background(
                                            if (valor == 1) {
                                                Color(0xFF245D48)
                                            } else {
                                                Color(0xFF29384C)
                                            },
                                            RoundedCornerShape(3.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        valor.toString(),
                                        color = KarnaughTexto,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        "Orden Gray de filas: ${datos.filasGray.joinToString(", ")}",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )

                    Text(
                        "Orden Gray de columnas: ${datos.columnasGray.joinToString(", ")}",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KarnaughPanel
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "MINTERMINOS",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        datos.minterminos.joinToString(", ") {
                            "m$it"
                        }.ifEmpty { "No hay minterminos." },
                        color = KarnaughTexto
                    )

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        "AGRUPACIONES",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    if (datos.grupos.isEmpty()) {
                        Text(
                            "No se necesitan agrupaciones.",
                            color = KarnaughTexto
                        )
                    }

                    datos.grupos.forEachIndexed { indice, grupo ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Grupo ${indice + 1}",
                                color = KarnaughVerde,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "PatrÃƒÆ’Ã‚Â³n: ${grupo.patron}",
                                color = Color.LightGray
                            )

                            Text(
                                "ExpresiÃƒÆ’Ã‚Â³n: ${grupo.expresion}",
                                color = KarnaughTexto
                            )

                            Text(
                                "Minterminos: ${grupo.minterminos.joinToString(", ")}",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        "FUNCIÃƒÆ’Ã¢â‚¬Å“N SIMPLIFICADA",
                        color = KarnaughVerde,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        datos.formulaSimplificada,
                        color = KarnaughTexto,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        "VERIFICACIÃƒÆ’Ã¢â‚¬Å“N DE EQUIVALENCIA",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    if (equivalencia == true) {
                        Text(
                            "CORRECTO: ambas funciones producen la misma salida en todas las combinaciones.",
                            color = KarnaughVerde,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            "ERROR: las funciones no son equivalentes. Revisa el resultado.",
                            color = Color(0xFFFF7777),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            val circuitoOriginal = GeneradorCircuitos.generar(
                formula
            )

            val circuitoSimplificado = GeneradorCircuitos.generar(
                datos.formulaSimplificada
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KarnaughPanel
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "CIRCUITO ORIGINAL",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Funcion: $formula",
                        color = KarnaughTexto
                    )

                    Text(
                        "Cantidad de compuertas: ${circuitoOriginal.cantidadCompuertas}",
                        color = KarnaughTexto,
                        fontWeight = FontWeight.Bold
                    )

                    CircuitoVisual(
                        raiz = circuitoOriginal.raiz
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KarnaughPanel
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "CIRCUITO DE LA FUNCIÃƒÆ’Ã¢â‚¬Å“N SIMPLIFICADA",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "FunciÃƒÆ’Ã‚Â³n: ${datos.formulaSimplificada}",
                        color = KarnaughTexto
                    )

                    Text(
                        "Cantidad de compuertas: ${circuitoSimplificado.cantidadCompuertas}",
                        color = KarnaughVerde,
                        fontWeight = FontWeight.Bold
                    )

                    CircuitoVisual(
                        raiz = circuitoSimplificado.raiz
                    )
                }
            }

            val comparacion = ComparadorCircuitos.comparar(
                formulaOriginal = formula,
                formulaSimplificada = datos.formulaSimplificada
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KarnaughPanel
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "ANÃLISIS DE OPTIMIZACIÃ“N",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Text(
                        "ComparaciÃ³n de compuertas lÃ³gicas",
                        color = KarnaughTexto
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Circuito original",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                            Text(
                                "${comparacion.compuertasOriginales}",
                                color = KarnaughTexto,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                "Circuito simplificado",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                            Text(
                                "${comparacion.compuertasSimplificadas}",
                                color = KarnaughVerde,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        "Compuertas reducidas: ${comparacion.compuertasReducidas}",
                        color = KarnaughTexto,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "ReducciÃ³n: ${
                            String.format(
                                java.util.Locale.US,
                                "%.2f",
                                comparacion.porcentajeReduccion
                            )
                        } %",
                        color = KarnaughVerde,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (comparacion.equivalentes) {
                            "EQUIVALENCIA FUNCIONAL: CONFIRMADA"
                        } else {
                            "EQUIVALENCIA FUNCIONAL: NO CONFIRMADA"
                        },
                        color = if (comparacion.equivalentes) {
                            KarnaughVerde
                        } else {
                            Color(0xFFFF7777)
                        },
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        comparacion.mensaje,
                        color = KarnaughTexto,
                        fontSize = 13.sp
                    )
                }
            }

            val variablesComparacion = (
                circuitoOriginal.variables + circuitoSimplificado.variables
            ).distinct().sorted()

            val entradasCompletas = variablesComparacion.associateWith {
                entradasSimulacion[it] ?: false
            }

            val simulacionOriginal = SimuladorCircuitos.simular(
                circuito = circuitoOriginal,
                entradas = entradasCompletas
            )

            val simulacionSimplificada = SimuladorCircuitos.simular(
                circuito = circuitoSimplificado,
                entradas = entradasCompletas
            )

            val salidasCoinciden =
                simulacionOriginal.resultadoSalida ==
                simulacionSimplificada.resultadoSalida

            LaunchedEffect(
                formula,
                datos,
                entradasCompletas,
                comparacion,
                simulacionOriginal.resultadoSalida,
                simulacionSimplificada.resultadoSalida
            ) {
                onResultadosActualizados(
                    DatosInformeTCC(
                        formulaOriginal = formula,
                        resultadoKarnaugh = datos,
                        tablaVerdad = GeneradorTablaVerdad.generar(formula),
                        comparacion = comparacion,
                        entradasSimulacion = entradasCompletas,
                        salidaOriginal = simulacionOriginal.resultadoSalida,
                        salidaSimplificada = simulacionSimplificada.resultadoSalida
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF29384C)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "SIMULACION COMPARATIVA",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Text(
                        "Los dos circuitos utilizan exactamente las mismas entradas.",
                        color = KarnaughTexto,
                        fontSize = 13.sp
                    )

                    variablesComparacion.forEach { variable ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "$variable = ${if (entradasCompletas[variable] == true) 1 else 0}",
                                color = KarnaughTexto,
                                fontWeight = FontWeight.Bold
                            )

                            Switch(
                                checked = entradasCompletas[variable] == true,
                                onCheckedChange = { nuevoValor ->
                                    entradasSimulacion =
                                        entradasSimulacion.toMutableMap().apply {
                                            this[variable] = nuevoValor
                                        }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = KarnaughVerde,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.DarkGray
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        "SALIDA DEL CIRCUITO ORIGINAL",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (simulacionOriginal.resultadoSalida)
                            "SALIDA = 1 | ACTIVADA"
                        else
                            "SALIDA = 0 | DESACTIVADA",
                        color = if (simulacionOriginal.resultadoSalida)
                            KarnaughVerde else Color(0xFFFF7777),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        "SALIDA DEL CIRCUITO SIMPLIFICADO",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (simulacionSimplificada.resultadoSalida)
                            "SALIDA = 1 | ACTIVADA"
                        else
                            "SALIDA = 0 | DESACTIVADA",
                        color = if (simulacionSimplificada.resultadoSalida)
                            KarnaughVerde else Color(0xFFFF7777),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        "COMPARACION AUTOMATICA",
                        color = KarnaughNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (salidasCoinciden)
                            "CORRECTO: AMBAS SALIDAS COINCIDEN"
                        else
                            "ADVERTENCIA: LAS SALIDAS SON DIFERENTES",
                        color = if (salidasCoinciden)
                            KarnaughVerde else Color(0xFFFF7777),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

