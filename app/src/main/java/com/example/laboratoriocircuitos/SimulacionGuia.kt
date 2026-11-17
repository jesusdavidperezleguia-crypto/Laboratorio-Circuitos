package com.example.laboratoriocircuitos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.GeneradorBooleano
import java.util.Locale

private val FondoGuia = Color(0xFF101827)
private val PanelGuia = Color(0xFF1C293B)
private val NaranjaGuia = Color(0xFFFF8733)
private val VerdeGuia = Color(0xFF70D69A)
private val TextoGuia = Color(0xFFF5F7FA)

private data class ResultadoAlarma(
    val w: Int,
    val i: Int,
    val c: Int,
    val z: Int
)

@Composable
fun PantallaSimulacionGuia() {

    var voltajeAlto by remember { mutableStateOf("5.0") }

    var entradaW by remember { mutableStateOf(false) }
    var entradaI by remember { mutableStateOf(false) }
    var entradaC by remember { mutableStateOf(false) }

    var precioNot by remember { mutableStateOf("2500") }
    var precioAnd by remember { mutableStateOf("2500") }
    var precioOr by remember { mutableStateOf("2500") }
    var precioProtoboard by remember { mutableStateOf("15000") }
    var precioCables by remember { mutableStateOf("5000") }
    var precioLedResistencia by remember { mutableStateOf("1000") }

    val formula = "Z = W*C' + W'*I"

    val salidaActual = GeneradorBooleano.evaluar(
        formula,
        mapOf(
            "W" to entradaW,
            "I" to entradaI,
            "C" to entradaC
        )
    )

    val resultados = remember {
        (0..1).flatMap { w ->
            (0..1).flatMap { i ->
                (0..1).map { c ->

                    val salida = GeneradorBooleano.evaluar(
                        formula,
                        mapOf(
                            "W" to (w == 1),
                            "I" to (i == 1),
                            "C" to (c == 1)
                        )
                    )

                    ResultadoAlarma(
                        w = w,
                        i = i,
                        c = c,
                        z = if (salida) 1 else 0
                    )
                }
            }
        }
    }

    val alto = voltajeAlto.toDoubleOrNull() ?: 5.0

    val costoNot = precioNot.toDoubleOrNull() ?: 0.0
    val costoAnd = precioAnd.toDoubleOrNull() ?: 0.0
    val costoOr = precioOr.toDoubleOrNull() ?: 0.0
    val costoProtoboard = precioProtoboard.toDoubleOrNull() ?: 0.0
    val costoCables = precioCables.toDoubleOrNull() ?: 0.0
    val costoLed = precioLedResistencia.toDoubleOrNull() ?: 0.0

    // Una unidad de cada circuito integrado es suficiente
    // para implementar las compuertas de esta expresión.
    val costoTotal =
        costoNot + costoAnd + costoOr +
        costoProtoboard + costoCables + costoLed

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PanelGuia, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "SIMULACIÓN COMPLETA DE LA GUÍA",
            color = NaranjaGuia,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            "PUNTO B. RESULTADOS DEL CIRCUITO",
            color = VerdeGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Expresión evaluada: $formula",
            color = TextoGuia
        )

        Text(
            "Cada fila se calcula automáticamente usando el evaluador booleano.",
            color = Color.LightGray,
            fontSize = 12.sp
        )

Text(
            "SIMULADOR INTERACTIVO",
            color = NaranjaGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Cambia las entradas para observar cómo responde el circuito.",
            color = TextoGuia,
            fontSize = 13.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("W - Conductor sentado: ${if (entradaW) 1 else 0}", color = TextoGuia)
            Switch(
                checked = entradaW,
                onCheckedChange = { entradaW = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("I - Llaves en ignición: ${if (entradaI) 1 else 0}", color = TextoGuia)
            Switch(
                checked = entradaI,
                onCheckedChange = { entradaI = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("C - Cinturón abrochado: ${if (entradaC) 1 else 0}", color = TextoGuia)
            Switch(
                checked = entradaC,
                onCheckedChange = { entradaC = it }
            )
        }

        HorizontalDivider(color = Color.Gray)

        Text(
            "ENTRADAS ACTUALES: W=${if (entradaW) 1 else 0}, I=${if (entradaI) 1 else 0}, C=${if (entradaC) 1 else 0}",
            color = TextoGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "SALIDA Z = ${if (salidaActual) 1 else 0}",
            color = if (salidaActual) VerdeGuia else Color.LightGray,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            if (salidaActual) "LED: ENCENDIDO" else "LED: APAGADO",
            color = if (salidaActual) VerdeGuia else Color.LightGray,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Voltaje simulado: ${String.format(Locale.US, "%.2f V", if (salidaActual) alto else 0.0)}",
            color = NaranjaGuia,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        TablaEncabezado(
            listOf("W", "I", "C", "Z", "LED", "V")
        )

        resultados.forEach { fila ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Celda(fila.w.toString())
                Celda(fila.i.toString())
                Celda(fila.c.toString())
                Celda(
                    fila.z.toString(),
                    VerdeGuia
                )
                Celda(
                    if (fila.z == 1) "ON" else "OFF",
                    if (fila.z == 1) VerdeGuia else Color.LightGray
                )
                Celda(
                    if (fila.z == 1) {
                        String.format(Locale.US, "%.2f V", alto)
                    } else {
                        "0.00 V"
                    },
                    NaranjaGuia
                )
            }

            HorizontalDivider(color = Color.DarkGray)
        }

        Text(
            "Voltaje alto de alimentación (V)",
            color = TextoGuia,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = voltajeAlto,
            onValueChange = { voltajeAlto = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            label = { Text("Voltaje lógico 1") }
        )

        Text(
            "Los voltajes son valores teóricos simulados. No son mediciones reales del protoboard.",
            color = Color(0xFFFFCC80),
            fontSize = 12.sp
        )

        HorizontalDivider(color = Color.Gray)

        Text(
            "PUNTO 7.1 Y 7.2. ESTIMACIÓN DE COSTOS",
            color = VerdeGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Ingresa los precios unitarios en pesos colombianos. Se considera una unidad de cada integrado.",
            color = TextoGuia,
            fontSize = 13.sp
        )

        CampoPrecio("74LS04 - NOT", precioNot) {
            precioNot = it
        }

        CampoPrecio("74LS08 - AND", precioAnd) {
            precioAnd = it
        }

        CampoPrecio("74LS32 - OR", precioOr) {
            precioOr = it
        }

        CampoPrecio("Protoboard", precioProtoboard) {
            precioProtoboard = it
        }

        CampoPrecio("Cables de conexión", precioCables) {
            precioCables = it
        }

        CampoPrecio("LED y resistencia", precioLedResistencia) {
            precioLedResistencia = it
        }

        HorizontalDivider(color = Color.Gray)

        Text(
            "CIRCUITO ORIGINAL",
            color = NaranjaGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Costo estimado: ${formatoPesos(costoTotal)}",
            color = TextoGuia,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "CIRCUITO REDUCIDO",
            color = VerdeGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Costo estimado: ${formatoPesos(costoTotal)}",
            color = TextoGuia,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "En esta guía no se ha proporcionado una expresión original diferente para comparar. La expresión evaluada ya está en forma simplificada. Por eso se muestra el mismo costo de implementación y no se inventa un ahorro.",
            color = Color.LightGray,
            fontSize = 12.sp
        )

        HorizontalDivider(color = Color.Gray)

        Text(
            "PUNTO 7.3. VENTAJAS DEL ÁLGEBRA DE BOOLE",
            color = VerdeGuia,
            fontWeight = FontWeight.Bold
        )

        val ventajas = listOf(
            "Facilita la simplificación de expresiones lógicas.",
            "Puede reducir la cantidad de compuertas.",
            "Puede disminuir el costo de los circuitos.",
            "Puede reducir el consumo de energía.",
            "Facilita el análisis y diseño de sistemas digitales.",
            "Permite obtener circuitos más sencillos."
        )

        ventajas.forEachIndexed { index, ventaja ->
            Text(
                "${index + 1}. $ventaja",
                color = TextoGuia,
                fontSize = 13.sp
            )
        }

        HorizontalDivider(color = Color.Gray)

        Text(
            "PUNTO 7.4. AND DE TRES ENTRADAS",
            color = VerdeGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Se utilizan dos compuertas AND de dos entradas conectadas en cascada.",
            color = TextoGuia,
            fontSize = 13.sp
        )

        Text(
            "A ──┐",
            color = TextoGuia,
            fontWeight = FontWeight.Bold
        )
        Text(
            "     AND 1 ── X ──┐",
            color = NaranjaGuia,
            fontWeight = FontWeight.Bold
        )
        Text(
            "B ──┘             AND 2 ── Z",
            color = TextoGuia,
            fontWeight = FontWeight.Bold
        )
        Text(
            "C ─────────────────┘",
            color = TextoGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Expresión: Z = (A · B) · C",
            color = VerdeGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "TABLA DE VERDAD AND",
            color = NaranjaGuia,
            fontWeight = FontWeight.Bold
        )

Text(
            "SIMULADOR INTERACTIVO",
            color = NaranjaGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Cambia las entradas para observar cómo responde el circuito.",
            color = TextoGuia,
            fontSize = 13.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("W - Conductor sentado: ${if (entradaW) 1 else 0}", color = TextoGuia)
            Switch(
                checked = entradaW,
                onCheckedChange = { entradaW = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("I - Llaves en ignición: ${if (entradaI) 1 else 0}", color = TextoGuia)
            Switch(
                checked = entradaI,
                onCheckedChange = { entradaI = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("C - Cinturón abrochado: ${if (entradaC) 1 else 0}", color = TextoGuia)
            Switch(
                checked = entradaC,
                onCheckedChange = { entradaC = it }
            )
        }

        HorizontalDivider(color = Color.Gray)

        Text(
            "ENTRADAS ACTUALES: W=${if (entradaW) 1 else 0}, I=${if (entradaI) 1 else 0}, C=${if (entradaC) 1 else 0}",
            color = TextoGuia,
            fontWeight = FontWeight.Bold
        )

        Text(
            "SALIDA Z = ${if (salidaActual) 1 else 0}",
            color = if (salidaActual) VerdeGuia else Color.LightGray,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            if (salidaActual) "LED: ENCENDIDO" else "LED: APAGADO",
            color = if (salidaActual) VerdeGuia else Color.LightGray,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Voltaje simulado: ${String.format(Locale.US, "%.2f V", if (salidaActual) alto else 0.0)}",
            color = NaranjaGuia,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        TablaEncabezado(listOf("A", "B", "C", "Z"))

        (0..1).forEach { a ->
            (0..1).forEach { b ->
                (0..1).forEach { c ->

                    val z = if (a == 1 && b == 1 && c == 1) 1 else 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Celda(a.toString())
                        Celda(b.toString())
                        Celda(c.toString())
                        Celda(z.toString(), VerdeGuia)
                    }
                }
            }
        }

        Text(
            "La salida Z solamente vale 1 cuando A, B y C son iguales a 1.",
            color = TextoGuia,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun TablaEncabezado(titulos: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoGuia, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        titulos.forEach {
            Celda(it, NaranjaGuia)
        }
    }
}

@Composable
private fun RowScope.Celda(
    valor: String,
    color: Color = TextoGuia
) {
    Text(
        text = valor,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun CampoPrecio(
    titulo: String,
    valor: String,
    alCambiar: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        label = { Text(titulo) }
    )
}

private fun formatoPesos(valor: Double): String {
    return "$ " + String.format(
        Locale("es", "CO"),
        "%,.0f",
        valor
    )
}


