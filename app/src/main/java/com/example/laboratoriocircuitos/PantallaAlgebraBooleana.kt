package com.example.laboratoriocircuitos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.CircuitoGenerado
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.GeneradorTablaVerdad
import com.example.laboratoriocircuitos.logic.TablaVerdadResultado
import com.example.laboratoriocircuitos.logic.ResultadoSimplificacion
import com.example.laboratoriocircuitos.logic.SimplificadorBooleano

private val AlgebraFondo = Color(0xFF101827)
private val AlgebraPanel = Color(0xFF1C293B)
private val AlgebraNaranja = Color(0xFFFF8733)
private val AlgebraVerde = Color(0xFF70D69A)
private val AlgebraTexto = Color.White
private val AlgebraSecundario = Color(0xFFB8C4D6)

private data class FuncionIndustrial(
    val nombre: String,
    val formula: String,
    val descripcion: String,
    val variables: String
)

private val funcionesIndustriales = listOf(
    FuncionIndustrial(
        nombre = "Alarma",
        formula = "AL = D+!E",
        descripcion = "Activa la alarma cuando existe una emergencia o el paso óptico está obstruido.",
        variables = "D: emergencia activada. E: paso óptico despejado."
    ),
    FuncionIndustrial(
        nombre = "Apertura",
        formula = "AP = A*B*C*!D*E*F",
        descripcion = "Autoriza la apertura cuando se cumplen todas las condiciones de seguridad y validación.",
        variables = "A: tarjeta autorizada. B: PIN correcto. C: puerta cerrada. D: emergencia. E: paso despejado. F: proximidad detectada."
    ),
    FuncionIndustrial(
        nombre = "Registro",
        formula = "R = A*B",
        descripcion = "Representa la validación conjunta de la tarjeta y el PIN.",
        variables = "A: tarjeta autorizada. B: PIN correcto."
    )
)

@Composable
fun PantallaAlgebraBooleana() {

    var formula by remember {
        mutableStateOf("F = A*B + A*C")
    }

    var funcionSeleccionada by remember {
        mutableStateOf<String?>(null)
    }

    var resultado by remember {
        mutableStateOf<ResultadoSimplificacion?>(null)
    }

    var circuito by remember {
        mutableStateOf<CircuitoGenerado?>(null)
    }
    var tablaVerdad by remember {
        mutableStateOf<TablaVerdadResultado?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val funcionActual = funcionesIndustriales.firstOrNull {
        it.nombre == funcionSeleccionada
    }

    fun cargarFuncion(funcion: FuncionIndustrial) {
        formula = funcion.formula
        funcionSeleccionada = funcion.nombre
        resultado = null
        circuito = null
        tablaVerdad = null
        error = null
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
            text = "SIMPLIFICADOR ALGEBRAICO",
            color = AlgebraVerde,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Reduce expresiones booleanas mediante leyes de identidad, absorción, complemento e idempotencia. Verifica la equivalencia con tablas de verdad.",
            color = AlgebraSecundario,
            fontSize = 13.sp
        )

        Text(
            text = "FASE 2 - FUNDAMENTOS DE LÓGICA DIGITAL",
            color = AlgebraVerde,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Análisis, simplificación y verificación de las funciones lógicas del proyecto SmartIndustries Caribe.",
            color = AlgebraSecundario,
            fontSize = 13.sp
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
                    text = "FUNCIONES INDUSTRIALES",
                    color = AlgebraNaranja,
                    fontWeight = FontWeight.Bold
                )

                funcionesIndustriales.forEach { funcion ->
                    Button(
                        onClick = {
                            cargarFuncion(funcion)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (
                                funcionSeleccionada == funcion.nombre
                            ) AlgebraVerde else Color(0xFF34445B),
                            contentColor = if (
                                funcionSeleccionada == funcion.nombre
                            ) Color.Black else AlgebraTexto
                        )
                    ) {
                        Text(
                            text = "CARGAR ${funcion.nombre.uppercase()}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                funcionActual?.let { funcion ->
                    HorizontalDivider(
                        color = AlgebraSecundario.copy(alpha = 0.3f)
                    )

                    Text(
                        text = funcion.descripcion,
                        color = AlgebraTexto,
                        fontSize = 13.sp
                    )

                    Text(
                        text = funcion.variables,
                        color = AlgebraSecundario,
                        fontSize = 12.sp
                    )
                }
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
                    text = "EXPRESIÓN BOOLEANA",
                    color = AlgebraNaranja,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = formula,
                    onValueChange = {
                        formula = it
                        funcionSeleccionada = null
                        resultado = null
                        circuito = null
                        tablaVerdad = null
                        error = null
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
                    text = "Operadores: * AND, + OR, ! NOT. También puedes utilizar paréntesis.",
                    color = AlgebraSecundario,
                    fontSize = 12.sp
                )

                Button(
                    onClick = {
                        try {
                            val nuevoResultado =
                                SimplificadorBooleano.simplificar(formula)

                            resultado = nuevoResultado

                            tablaVerdad = if (nuevoResultado.equivalente) {
                                GeneradorTablaVerdad.generar(formula)
                            } else {
                                null
                            }

                            circuito = if (nuevoResultado.equivalente) {
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
                            tablaVerdad = null
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
                        text = "ANALIZAR Y SIMPLIFICAR",
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
                        text = "RESULTADO DEL ANÁLISIS",
                        color = AlgebraNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Expresión original: ${res.original}",
                        color = AlgebraTexto
                    )

                    Text(
                        text = "Expresión resultante: ${res.simplificada}",
                        color = AlgebraVerde,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Variables: ${res.variables.joinToString(", ")}",
                        color = AlgebraSecundario
                    )

                    Text(
                        text = "Compuertas del circuito: ${circuito?.cantidadCompuertas ?: 0}",
                        color = AlgebraSecundario
                    )

                    HorizontalDivider(
                        color = AlgebraSecundario.copy(alpha = 0.3f)
                    )

                    Text(
                        text = if (res.equivalente) {
                            "VERIFICACIÓN CORRECTA: ambas expresiones producen el mismo resultado para todas las combinaciones evaluadas."
                        } else {
                            "ADVERTENCIA: no se confirmó la equivalencia lógica."
                        },
                        color = if (res.equivalente) {
                            AlgebraVerde
                        } else {
                            Color(0xFFFF7777)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    if (res.pasos.isEmpty()) {
                        Text(
                            text = "No se encontraron transformaciones adicionales con las reglas disponibles. La expresión se conserva y se verifica su equivalencia.",
                            color = AlgebraSecundario,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            tablaVerdad?.let { tabla ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = AlgebraPanel
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "TABLA DE VERDAD",
                            color = AlgebraNaranja,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${tabla.variables.size} variables | ${tabla.filas.size} combinaciones evaluadas",
                            color = AlgebraSecundario,
                            fontSize = 12.sp
                        )

                        val desplazamientoTabla = rememberScrollState()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(desplazamientoTabla),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            tabla.variables.forEach { variable ->
                                Text(
                                    text = variable,
                                    modifier = Modifier.width(48.dp).padding(5.dp),
                                    color = AlgebraNaranja,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "SAL",
                                modifier = Modifier.width(52.dp).padding(5.dp),
                                color = AlgebraVerde,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(
                            color = AlgebraSecundario.copy(alpha = 0.4f)
                        )

                        tabla.filas.forEachIndexed { indice, fila ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(desplazamientoTabla),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                tabla.variables.forEach { variable ->
                                    Text(
                                        text = if (fila.entradas[variable] == true) "1" else "0",
                                        modifier = Modifier.width(48.dp).padding(5.dp),
                                        color = AlgebraTexto,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    text = if (fila.salida) "1" else "0",
                                    modifier = Modifier.width(52.dp).padding(5.dp),
                                    color = if (fila.salida) AlgebraVerde else AlgebraSecundario,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            if (indice < tabla.filas.lastIndex) {
                                HorizontalDivider(
                                    color = AlgebraSecundario.copy(alpha = 0.12f)
                                )
                            }
                        }
                    }
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
                        text = "PROCEDIMIENTO DE SIMPLIFICACIÓN",
                        color = AlgebraNaranja,
                        fontWeight = FontWeight.Bold
                    )

                    if (res.pasos.isEmpty()) {
                        Text(
                            text = "La función no tuvo transformaciones adicionales mediante las reglas implementadas.",
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
                            text = "CIRCUITO DE LA FUNCIÓN",
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "FUNDAMENTOS Y REGLAS BOOLEANAS",
                    color = AlgebraNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "AND (*): la salida es 1 cuando todas las entradas son 1.\n\nOR (+): la salida es 1 cuando al menos una entrada es 1.\n\nNOT (!): invierte el valor lógico de una entrada.",
                    color = AlgebraTexto,
                    fontSize = 13.sp,
                    lineHeight = 21.sp
                )

                HorizontalDivider(
                    color = AlgebraSecundario.copy(alpha = 0.3f)
                )

                Text(
                    text = "REGLAS IMPLEMENTADAS",
                    color = AlgebraNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "• Identidad lógica\n• Dominación\n• Idempotencia\n• Complemento\n• Absorción\n• Doble negación\n• Propiedad distributiva",
                    color = AlgebraTexto,
                    lineHeight = 22.sp
                )

                Text(
                    text = "La herramienta aplica las reglas disponibles y comprueba la equivalencia mediante la evaluación de las combinaciones de entrada. No garantiza encontrar siempre la forma mínima global.",
                    color = AlgebraSecundario,
                    fontSize = 12.sp
                )
            }
        }
    }
}



