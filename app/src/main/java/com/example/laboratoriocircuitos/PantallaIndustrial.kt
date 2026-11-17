package com.example.laboratoriocircuitos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.ConversorNumeracion
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.GeneradorTablaVerdad
import com.example.laboratoriocircuitos.logic.SimuladorCircuitos

private val IndustrialFondo = Color(0xFF101827)
private val IndustrialPanel = Color(0xFF1C293B)
private val IndustrialNaranja = Color(0xFFFF8733)
private val IndustrialVerde = Color(0xFF70D69A)
private val IndustrialTexto = Color(0xFFF5F7FA)
private val IndustrialSecundario = Color(0xFFB8C4D6)

@Composable
fun PantallaIndustrial() {

    var numeroEmpleado by remember { mutableStateOf("25") }

    var tarjetaAutorizada by remember { mutableStateOf(true) }
    var pinCorrecto by remember { mutableStateOf(true) }
    var puertaCerrada by remember { mutableStateOf(true) }
    var emergenciaActivada by remember { mutableStateOf(false) }
    var pasoOpticoDespejado by remember { mutableStateOf(true) }
    var proximidadDetectada by remember { mutableStateOf(false) }

    val formula = "S = A*B*C*!D*E*F"

    val circuito = remember {
        GeneradorCircuitos.generar(formula)
    }

    val tablaVerdad = remember {
        GeneradorTablaVerdad.generar(formula)
    }

    val numeracion = numeroEmpleado
        .toIntOrNull()
        ?.takeIf { it in 0..9999 }
        ?.let { ConversorNumeracion.convertir(it) }

    val entradasActuales = mapOf(
        "A" to tarjetaAutorizada,
        "B" to pinCorrecto,
        "C" to puertaCerrada,
        "D" to emergenciaActivada,
        "E" to pasoOpticoDespejado,
        "F" to proximidadDetectada
    )

    val simulacion = remember(
        tarjetaAutorizada,
        pinCorrecto,
        puertaCerrada,
        emergenciaActivada,
        pasoOpticoDespejado,
        proximidadDetectada
    ) {
        SimuladorCircuitos.simular(
            circuito = circuito,
            entradas = entradasActuales
        )
    }

    val accesoPermitido = simulacion.resultadoSalida

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "FASE 1 · ANÁLISIS DE REQUERIMIENTOS",
            color = IndustrialNaranja,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "SMARTINDUSTRIES CARIBE",
            color = IndustrialTexto,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Modelado de entradas, identificación del personal y validación digital del sistema de control de acceso.",
            color = IndustrialSecundario,
            fontSize = 13.sp
        )

        // REGISTRO DEL EMPLEADO

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "REGISTRO Y CODIFICACIÓN DEL PERSONAL",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ingresa el número de identificación del empleado para observar su representación en diferentes sistemas de numeración.",
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = numeroEmpleado,
                    onValueChange = { nuevoValor ->
                        if (nuevoValor.length <= 4 && nuevoValor.all { it.isDigit() }) {
                            numeroEmpleado = nuevoValor
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Número de empleado (0 a 9999)")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                if (numeracion != null) {

                    FilaDatoIndustrial(
                        "Decimal",
                        numeracion.decimal.toString()
                    )

                    FilaDatoIndustrial(
                        "Binario",
                        numeracion.binario
                    )

                    FilaDatoIndustrial(
                        "BCD",
                        numeracion.bcd
                    )

                    Text(
                        text = "El código BCD representa cada dígito decimal mediante cuatro bits.",
                        color = IndustrialSecundario,
                        fontSize = 11.sp
                    )

                } else {

                    Text(
                        text = "Introduce un número válido entre 0 y 9999.",
                        color = IndustrialNaranja,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // ENTRADAS DIGITALES

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
                    text = "ENTRADAS DEL SISTEMA INDUSTRIAL",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Cada interruptor representa una señal digital de entrada.",
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )

                EntradaIndustrial(
                    titulo = "A - Tarjeta autorizada",
                    descripcion = "La tarjeta presentada está registrada.",
                    valor = tarjetaAutorizada,
                    cambiar = { tarjetaAutorizada = it }
                )

                HorizontalDivider(
                    color = IndustrialSecundario.copy(alpha = 0.25f)
                )

                EntradaIndustrial(
                    titulo = "B - PIN correcto",
                    descripcion = "El código ingresado es válido.",
                    valor = pinCorrecto,
                    cambiar = { pinCorrecto = it }
                )

                HorizontalDivider(
                    color = IndustrialSecundario.copy(alpha = 0.25f)
                )

                EntradaIndustrial(
                    titulo = "C - Puerta cerrada",
                    descripcion = "El sensor indica que la puerta está cerrada.",
                    valor = puertaCerrada,
                    cambiar = { puertaCerrada = it }
                )

                HorizontalDivider(
                    color = IndustrialSecundario.copy(alpha = 0.25f)
                )

                EntradaIndustrial(
                    titulo = "D - Parada de emergencia",
                    descripcion = "Activa esta entrada para bloquear el acceso.",
                    valor = emergenciaActivada,
                    cambiar = { emergenciaActivada = it }
                )
            }
        }

        PanelSensoresIndustriales(
            emergenciaActivada = emergenciaActivada,
            pasoOpticoDespejado = pasoOpticoDespejado,
            cambiarPasoOptico = { pasoOpticoDespejado = it },
            proximidadDetectada = proximidadDetectada,
            cambiarProximidad = { proximidadDetectada = it }
        )

        PanelSalidasIndustriales(entradas = entradasActuales)

        // RESULTADO DE LA SIMULACIÓN

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
                    text = if (accesoPermitido) {
                        "ACCESO PERMITIDO"
                    } else {
                        "ACCESO BLOQUEADO"
                    },
                    color = if (accesoPermitido) {
                        IndustrialVerde
                    } else {
                        IndustrialNaranja
                    },
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = simulacion.mensaje,
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )
            }
        }

        // FUNCIÓN BOOLEANA

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
                    fontSize = 16.sp
                )

                Text(
                    text = "La salida se activa cuando la tarjeta está autorizada, el PIN es correcto, la puerta está cerrada y la emergencia está desactivada.",
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )

                HorizontalDivider(
                    color = IndustrialSecundario.copy(alpha = 0.25f)
                )

                Text(
                    text = "IDENTIFICACIÓN DE VARIABLES",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                FilaDatoIndustrial("A", "Tarjeta autorizada")
                FilaDatoIndustrial("B", "PIN correcto")
                FilaDatoIndustrial("C", "Puerta cerrada")
                FilaDatoIndustrial("D", "Parada de emergencia")
                FilaDatoIndustrial("S", "Acceso permitido")
            }
        }

        // DIAGRAMA DEL CIRCUITO

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

                Text(
                    text = "Representación de las compuertas que implementan la función de acceso.",
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )

                CircuitoVisual(
                    raiz = circuito.raiz
                )
            }
        }

        // TABLA DE VERDAD

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
                    text = "TABLA DE VERDAD",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Evaluación de las 16 combinaciones posibles de las cuatro entradas digitales.",
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )

                FilaTablaIndustrial(
                    valores = listOf("A", "B", "C", "D", "S"),
                    encabezado = true
                )

                HorizontalDivider(
                    color = IndustrialNaranja.copy(alpha = 0.6f)
                )

                tablaVerdad.filas.forEachIndexed { indice, fila ->

                    FilaTablaIndustrial(
                        valores = listOf(
                            if (fila.entradas["A"] == true) "1" else "0",
                            if (fila.entradas["B"] == true) "1" else "0",
                            if (fila.entradas["C"] == true) "1" else "0",
                            if (fila.entradas["D"] == true) "1" else "0",
                            if (fila.salida) "1" else "0"
                        ),
                        encabezado = false,
                        destacado = fila.salida
                    )

                    if (indice < tablaVerdad.filas.lastIndex) {
                        HorizontalDivider(
                            color = IndustrialSecundario.copy(alpha = 0.12f)
                        )
                    }
                }

                Text(
                    text = "Total de combinaciones: ${tablaVerdad.filas.size}",
                    color = IndustrialVerde,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Text(
                    text = "La salida S vale 1 únicamente cuando A=1, B=1, C=1 y D=0.",
                    color = IndustrialSecundario,
                    fontSize = 12.sp
                )
            }
        }

        // ESTADO ACTUAL

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
                    text = "ESTADO ACTUAL DE LAS ENTRADAS",
                    color = IndustrialNaranja,
                    fontWeight = FontWeight.Bold
                )

                FilaEstadoIndustrial("A - Tarjeta", tarjetaAutorizada)
                FilaEstadoIndustrial("B - PIN", pinCorrecto)
                FilaEstadoIndustrial("C - Puerta", puertaCerrada)
                FilaEstadoIndustrial("D - Emergencia", emergenciaActivada)

                HorizontalDivider(
                    color = IndustrialSecundario.copy(alpha = 0.25f)
                )

                FilaEstadoIndustrial(
                    "S - Salida",
                    accesoPermitido
                )
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

        Column(
            modifier = Modifier.weight(1f)
        ) {

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

@Composable
private fun FilaDatoIndustrial(
    nombre: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = nombre,
            color = IndustrialSecundario,
            fontSize = 13.sp
        )

        Text(
            text = valor,
            color = IndustrialTexto,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FilaTablaIndustrial(
    valores: List<String>,
    encabezado: Boolean,
    destacado: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        valores.forEach { valor ->

            Text(
                text = valor,
                modifier = Modifier.weight(1f),
                color = when {
                    encabezado -> IndustrialNaranja
                    destacado && valor == "1" -> IndustrialVerde
                    else -> IndustrialTexto
                },
                fontSize = 13.sp,
                fontWeight = if (encabezado || destacado) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}


