package com.example.laboratoriocircuitos

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.ResultadoSolucionEjercicio
import com.example.laboratoriocircuitos.logic.SolucionadorEjercicios

private val NaranjaSolucion = Color(0xFFFF8733)
private val VerdeSolucion = Color(0xFF70D69A)
private val PanelSolucion = Color(0xFF1C293B)
private val TextoSolucion = Color.White

@Composable
fun TarjetaSolucionActividad(formula: String) {

    var resultado by remember(formula) {
        mutableStateOf<ResultadoSolucionEjercicio?>(null)
    }

    var error by remember(formula) {
        mutableStateOf<String?>(null)
    }
    val context = LocalContext.current
    val resultadoActual by rememberUpdatedState(resultado)

    val selectorPDF = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            try {
                val solucion = resultadoActual
                    ?: throw IllegalStateException("Primero debes resolver la actividad.")

                context.contentResolver.openOutputStream(uri)?.use { salida ->
                    GeneradorPDFActividad.generar(solucion, salida)
                } ?: throw IllegalStateException("No se pudo guardar el documento.")
            } catch (e: Exception) {
                error = "Error al generar el PDF: ${e.message}"
            }
        }
    }


    Column(modifier = Modifier.fillMaxWidth()) {

        Button(
            onClick = {
                try {
                    error = null
                    resultado = SolucionadorEjercicios.solucionar(formula)
                } catch (e: Exception) {
                    resultado = null
                    error = e.message ?: "No fue posible resolver esta fórmula."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = NaranjaSolucion,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "RESOLVER ACTIVIDAD",
                fontWeight = FontWeight.Bold
            )
        }

        error?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = it,
                color = Color(0xFFFF7777),
                fontSize = 13.sp
            )
        }

        resultado?.let { solucion ->

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PanelSolucion
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "RESULTADO DE LA ACTIVIDAD",
                        color = VerdeSolucion,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = solucion.mensaje,
                        color = if (
                            solucion.verificacionTablaCorrecta &&
                            solucion.verificacionEquivalenciaCorrecta
                        ) VerdeSolucion else NaranjaSolucion,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "1. FÓRMULA ANALIZADA",
                        color = NaranjaSolucion,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = solucion.formulaOriginal,
                        color = TextoSolucion
                    )

                    Text(
                        text = "Expresión normalizada: ${solucion.formulaNormalizada}",
                        color = TextoSolucion
                    )

                    Text(
                        text = "Variables: ${solucion.variables.joinToString(", ")}",
                        color = TextoSolucion
                    )

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        text = "2. TABLA DE VERDAD",
                        color = NaranjaSolucion,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier.horizontalScroll(
                            rememberScrollState()
                        )
                    ) {
                        Row {
                            (solucion.tablaVerdad.variables + "Z").forEach {
                                Text(
                                    text = it,
                                    modifier = Modifier.width(48.dp),
                                    color = VerdeSolucion,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        solucion.tablaVerdad.filas.forEach { fila ->
                            Row {
                                solucion.tablaVerdad.variables.forEach { variable ->
                                    Text(
                                        text = if (fila.entradas[variable] == true) "1" else "0",
                                        modifier = Modifier.width(48.dp),
                                        color = TextoSolucion
                                    )
                                }

                                Text(
                                    text = if (fila.salida) "1" else "0",
                                    modifier = Modifier.width(48.dp),
                                    color = VerdeSolucion,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Total de combinaciones: ${solucion.tablaVerdad.filas.size}",
                        color = TextoSolucion
                    )

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        text = "3. CIRCUITO ORIGINAL",
                        color = NaranjaSolucion,
                        fontWeight = FontWeight.Bold
                    )

                    CircuitoVisual(solucion.circuito.raiz)

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        text = "4. SIMPLIFICACIÓN BOOLEANA",
                        color = NaranjaSolucion,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = solucion.simplificacion.simplificada,
                        color = VerdeSolucion,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (solucion.simplificacion.pasos.isEmpty()) {
                        Text(
                            text = "No se encontraron pasos adicionales de simplificación.",
                            color = TextoSolucion
                        )
                    } else {
                        solucion.simplificacion.pasos.forEachIndexed { index, paso ->
                            Text(
                                text = "Paso ${index + 1}: ${paso.regla}",
                                color = VerdeSolucion,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${paso.antes}  →  ${paso.despues}",
                                color = TextoSolucion
                            )
                            Text(
                                text = paso.explicacion,
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        text = "5. CIRCUITO SIMPLIFICADO",
                        color = NaranjaSolucion,
                        fontWeight = FontWeight.Bold
                    )

                    CircuitoVisual(solucion.circuitoSimplificado.raiz)

                    HorizontalDivider(color = Color.Gray)

                    Text(
                        text = "6. VERIFICACIÓN",
                        color = NaranjaSolucion,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Tabla de verdad: ${
                            if (solucion.verificacionTablaCorrecta) "CORRECTA" else "REVISAR"
                        }",
                        color = if (solucion.verificacionTablaCorrecta) VerdeSolucion else NaranjaSolucion
                    )

                    Text(
                        text = "Equivalencia de circuitos: ${
                            if (solucion.verificacionEquivalenciaCorrecta) "CONFIRMADA" else "NO CONFIRMADA"
                        }",
                        color = if (solucion.verificacionEquivalenciaCorrecta) VerdeSolucion else NaranjaSolucion
                    )

                    Button(
                        onClick = {
                            val nombre = "Informe_Circuitos_${System.currentTimeMillis()}"
                            selectorPDF.launch("$nombre.pdf")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NaranjaSolucion,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "GENERAR PDF PARA ENTREGAR",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


