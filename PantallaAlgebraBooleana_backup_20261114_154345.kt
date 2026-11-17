package com.example.laboratoriocircuitos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.CircuitoGenerado
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.ResultadoSimplificacion
import com.example.laboratoriocircuitos.logic.SimplificadorBooleano

private val AlgebraFondo = Color(0xFF101827)
private val AlgebraPanel = Color(0xFF1C293B)
private val AlgebraNaranja = Color(0xFFFF8733)
private val AlgebraVerde = Color(0xFF70D69A)
private val AlgebraTexto = Color.White
private val AlgebraSecundario = Color(0xFFB8C4D6)

@Composable
fun PantallaAlgebraBooleana() {

    var formula by remember {
        mutableStateOf("F = A*B + A*C")
    }

    var resultado by remember {
        mutableStateOf<ResultadoSimplificacion?>(null)
    }

    var circuito by remember {
        mutableStateOf<CircuitoGenerado?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlgebraFondo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "ÁLGEBRA BOOLEANA",
            color = AlgebraNaranja,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Simplificación de expresiones lógicas",
            color = AlgebraSecundario,
            fontSize = 14.sp
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = AlgebraPanel
            ),
            shape = RoundedCornerShape(14.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "EXPRESIÓN ORIGINAL",
                    color = AlgebraNaranja,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = formula,
                    onValueChange = {
                        formula = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Ingresa la función lógica")
                    },
                    placeholder = {
                        Text("Ejemplo: F = A*B + A*C")
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AlgebraTexto,
                        unfocusedTextColor = AlgebraTexto,
                        focusedBorderColor = AlgebraNaranja,
                        unfocusedBorderColor = AlgebraSecundario,
                        focusedLabelColor = AlgebraNaranja,
                        unfocusedLabelColor = AlgebraSecundario,
                        cursorColor = AlgebraNaranja
                    )
                )

                Text(
                    text = "Operadores: * AND, + OR, ! NOT, paréntesis para agrupar.",
                    color = AlgebraSecundario,
                    fontSize = 12.sp
                )

                Button(
                    onClick = {

                        try {
                            val nuevoResultado =
                                SimplificadorBooleano.simplificar(formula)

                            resultado = nuevoResultado

                            circuito =
                                if (nuevoResultado.equivalente) {
                                    GeneradorCircuitos.generar(
                                        nuevoResultado.simplificada
                                    )
                                } else {
                                    null
                                }

                            error = null

                        } catch (e: Exception) {

                            resultado = null
                            circuito = null
                            error = e.message
                                ?: "No se pudo procesar la expresión."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlgebraNaranja,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "SIMPLIFICAR EXPRESIÓN",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        error?.let {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF512B2B)
                )
            ) {
                Text(
                    text = it,
                    color = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        resultado?.let { res ->

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AlgebraPanel
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "RESULTADO",
                        color = AlgebraNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Original: ${res.original}",
                        color = AlgebraTexto
                    )

                    Text(
                        text = "Simplificada: ${res.simplificada}",
                        color = AlgebraVerde,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Variables: ${res.variables.joinToString(", ")}",
                        color = AlgebraSecundario
                    )

                    HorizontalDivider(
                        color = AlgebraSecundario.copy(alpha = 0.3f)
                    )

                    Text(
                        text = if (res.equivalente) {
                            "VERIFICACIÓN: Las expresiones son equivalentes."
                        } else {
                            "ADVERTENCIA: No se pudo confirmar la equivalencia."
                        },
                        color = if (res.equivalente) {
                            AlgebraVerde
                        } else {
                            Color(0xFFFF7777)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = AlgebraPanel
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "PROCEDIMIENTO",
                        color = AlgebraNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    if (res.pasos.isEmpty()) {

                        Text(
                            text = "No se encontraron reglas adicionales para aplicar con las reglas disponibles.",
                            color = AlgebraSecundario
                        )

                    } else {

                        res.pasos.forEachIndexed { indice, paso ->

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        AlgebraSecundario.copy(alpha = 0.25f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = "PASO ${indice + 1}: ${paso.regla}",
                                    color = AlgebraNaranja,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Antes: ${paso.antes}",
                                    color = AlgebraTexto
                                )

                                Text(
                                    text = "Después: ${paso.despues}",
                                    color = AlgebraVerde
                                )

                                Text(
                                    text = paso.explicacion,
                                    color = AlgebraSecundario,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            if (res.equivalente && circuito != null) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = AlgebraPanel
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            text = "CIRCUITO SIMPLIFICADO",
                            color = AlgebraNaranja,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        CircuitoVisual(
                            raiz = circuito!!.raiz
                        )
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = AlgebraPanel
            )
        ) {

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "REGLAS DISPONIBLES",
                    color = AlgebraNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "• Identidad lógica\n• Dominación\n• Idempotencia\n• Complemento\n• Absorción\n• Doble negación",
                    color = AlgebraTexto,
                    lineHeight = 22.sp
                )

                Text(
                    text = "La simplificación automática utiliza las reglas implementadas y verifica el resultado mediante las combinaciones de entradas.",
                    color = AlgebraSecundario,
                    fontSize = 12.sp
                )
            }
        }
    }
}
