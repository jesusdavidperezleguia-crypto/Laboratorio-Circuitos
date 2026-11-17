package com.example.laboratoriocircuitos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laboratoriocircuitos.logic.*
import kotlin.math.hypot

private val EditorFondo = Color(0xFF101827)
private val EditorPanel = Color(0xFF1C293B)
private val EditorNaranja = Color(0xFFFF8733)
private val EditorVerde = Color(0xFF70D69A)
private val EditorAzul = Color(0xFF64B5F6)

private const val ANCHO_COMPONENTE = 112f
private const val ALTO_COMPONENTE = 68f

@Composable
fun PantallaEditorCircuitos(
    onCircuitoChange: (
        List<ComponenteEditor>,
        List<ConexionEditor>
    ) -> Unit = { _, _ -> }
) {

    val componentes = remember {
        mutableStateListOf<ComponenteEditor>()
    }

    val conexiones = remember {
        mutableStateListOf<ConexionEditor>()
    }

    var siguienteId by remember { mutableIntStateOf(1) }
    var origenPendiente by remember { mutableStateOf("") }
    var mensaje by remember {
        mutableStateOf("Agrega componentes para comenzar.")
    }

    
    SideEffect {
        onCircuitoChange(
            componentes.toList(),
            conexiones.toList()
        )
    }

    val resultado = runCatching {
        MotorEditorCircuitos.evaluar(
            componentes.toList(),
            conexiones.toList()
        )
    }

    fun agregar(tipo: TipoComponenteEditor) {
        val numero = componentes.size

        val nuevo = ComponenteEditor(
            id = "C${siguienteId++}",
            tipo = tipo,
            x = 20f + (numero % 2) * 170f,
            y = 20f + ((numero / 2) % 4) * 120f
        )

        componentes.add(nuevo)
        mensaje = "${tipo.nombre} agregado. Puedes moverlo con el dedo."
    }

    fun cambiarValor(id: String) {
        val indice = componentes.indexOfFirst { it.id == id }

        if (indice >= 0) {
            val actual = componentes[indice]
            componentes[indice] = actual.copy(
                valor = !actual.valor
            )
        }
    }

    fun conectar(origen: String, destino: String, entrada: Int) {
        if (origen == destino) {
            mensaje = "No puedes conectar un componente consigo mismo."
            return
        }

        val conexion = ConexionEditor(
            origenId = origen,
            destinoId = destino,
            entradaDestino = entrada
        )

        conexiones.removeAll {
            it.destinoId == destino &&
                it.entradaDestino == entrada
        }

        conexiones.add(conexion)
        mensaje = "Cable conectado: $origen → $destino."
        origenPendiente = ""
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "LABORATORIO GRÁFICO DE CIRCUITOS",
            color = EditorNaranja,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            "Agrega componentes, arrástralos y conecta sus terminales.",
            color = Color.White
        )

        // PANEL DE COMPONENTES

        Card(
            colors = CardDefaults.cardColors(
                containerColor = EditorPanel
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "COMPONENTES",
                    color = EditorNaranja,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { agregar(TipoComponenteEditor.ENTRADA) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EditorAzul
                        )
                    ) {
                        Text("ENTRADA")
                    }

                    Button(
                        onClick = { agregar(TipoComponenteEditor.AND) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("AND")
                    }

                    Button(
                        onClick = { agregar(TipoComponenteEditor.OR) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("OR")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { agregar(TipoComponenteEditor.NOT) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("NOT")
                    }

                    Button(
                        onClick = {
                            agregar(TipoComponenteEditor.FLIP_FLOP_JK)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("JK")
                    }

                    Button(
                        onClick = {
                            agregar(TipoComponenteEditor.FLIP_FLOP_D)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("FLIP-FLOP D")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { agregar(TipoComponenteEditor.RELOJ) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("RELOJ")
                    }

                    Button(
                        onClick = { agregar(TipoComponenteEditor.LED) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EditorVerde
                        )
                    ) {
                        Text("LED")
                    }
                }
            }
        }

        // MESA GRÁFICA

        Card(
            colors = CardDefaults.cardColors(
                containerColor = EditorPanel
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "MESA DE TRABAJO",
                    color = EditorNaranja,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Salida: círculo derecho. Entradas: círculos izquierdos.",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )

                val density = LocalDensity.current

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(560.dp)
                        .background(
                            Color(0xFF0B1320),
                            RoundedCornerShape(12.dp)
                        )
                        .pointerInput(componentes.size) {
                            detectTapGestures { posicion ->

                                val x = posicion.x / density.density
                                val y = posicion.y / density.density

                                var terminalEncontrado = false

                                // Primero se comprueban las salidas.
                                for (componente in componentes) {

                                    val salidaX =
                                        componente.x + ANCHO_COMPONENTE

                                    val salidaY =
                                        componente.y + ALTO_COMPONENTE / 2f

                                    val distancia = hypot(
                                        x - salidaX,
                                        y - salidaY
                                    )

                                    if (
                                        distancia < 25f &&
                                        componente.tipo != TipoComponenteEditor.LED
                                    ) {
                                        origenPendiente = componente.id
                                        mensaje =
                                            "Salida ${componente.id} seleccionada. Ahora toca una entrada."
                                        terminalEncontrado = true
                                        break
                                    }
                                }

                                // Después se comprueban las entradas.
                                if (!terminalEncontrado &&
                                    origenPendiente.isNotEmpty()
                                ) {
                                    for (componente in componentes) {

                                        if (
                                            componente.tipo.cantidadEntradas == 0
                                        ) continue

                                        val posiciones = when (
                                            componente.tipo.cantidadEntradas
                                        ) {
                                            1 -> listOf(
                                                componente.y + ALTO_COMPONENTE / 2f
                                            )

                                            2 -> listOf(
                                                componente.y + 22f,
                                                componente.y + 46f
                                            )

                                            else -> emptyList()
                                        }

                                        posiciones.forEachIndexed { indice, entradaY ->

                                            val distancia = hypot(
                                                x - componente.x,
                                                y - entradaY
                                            )

                                            if (
                                                distancia < 25f &&
                                                origenPendiente != componente.id
                                            ) {
                                                conectar(
                                                    origenPendiente,
                                                    componente.id,
                                                    indice
                                                )
                                                terminalEncontrado = true
                                            }
                                        }

                                        if (terminalEncontrado) break
                                    }
                                }

                                if (!terminalEncontrado &&
                                    origenPendiente.isEmpty()
                                ) {
                                    val componente = componentes.firstOrNull {
                                        x >= it.x &&
                                            x <= it.x + ANCHO_COMPONENTE &&
                                            y >= it.y &&
                                            y <= it.y + ALTO_COMPONENTE
                                    }

                                    if (componente != null) {
                                        mensaje =
                                            "${componente.id}: ${componente.tipo.nombre}"
                                    }
                                }
                            }
                        }
                        .pointerInput(componentes.size) {

                            var componenteMovido: String? = null

                            detectDragGestures(
                                onDragStart = { posicion ->

                                    val x = posicion.x / density.density
                                    val y = posicion.y / density.density

                                    componenteMovido = componentes
                                        .lastOrNull {
                                            x >= it.x &&
                                                x <= it.x + ANCHO_COMPONENTE &&
                                                y >= it.y &&
                                                y <= it.y + ALTO_COMPONENTE
                                        }?.id
                                },
                                onDrag = { cambio, desplazamiento ->

                                    cambio.consume()

                                    val id = componenteMovido ?: return@detectDragGestures

                                    val indice = componentes.indexOfFirst {
                                        it.id == id
                                    }

                                    if (indice >= 0) {
                                        val actual = componentes[indice]

                                        componentes[indice] = actual.copy(
                                            x = (actual.x +
                                                desplazamiento.x / density.density)
                                                .coerceAtLeast(0f),

                                            y = (actual.y +
                                                desplazamiento.y / density.density)
                                                .coerceIn(0f, 485f)
                                        )
                                    }
                                },
                                onDragEnd = {
                                    componenteMovido = null
                                },
                                onDragCancel = {
                                    componenteMovido = null
                                }
                            )
                        }
                ) {

                    val escala = density.density

                    // DIBUJAR LOS CABLES

                    conexiones.forEach { conexion ->

                        val origen = componentes.firstOrNull {
                            it.id == conexion.origenId
                        }

                        val destino = componentes.firstOrNull {
                            it.id == conexion.destinoId
                        }

                        if (origen != null && destino != null) {

                            val inicio = Offset(
                                (origen.x + ANCHO_COMPONENTE) * escala,
                                (origen.y + ALTO_COMPONENTE / 2f) * escala
                            )

                            val posiciones = when (
                                destino.tipo.cantidadEntradas
                            ) {
                                1 -> listOf(
                                    destino.y + ALTO_COMPONENTE / 2f
                                )

                                2 -> listOf(
                                    destino.y + 22f,
                                    destino.y + 46f
                                )

                                else -> emptyList()
                            }

                            val entradaY = posiciones.getOrNull(
                                conexion.entradaDestino
                            ) ?: return@forEach

                            val fin = Offset(
                                destino.x * escala,
                                entradaY * escala
                            )

                            val mitadX = (inicio.x + fin.x) / 2f

                            val camino = Path().apply {
                                moveTo(inicio.x, inicio.y)
                                lineTo(mitadX, inicio.y)
                                lineTo(mitadX, fin.y)
                                lineTo(fin.x, fin.y)
                            }

                            drawPath(
                                path = camino,
                                color = EditorVerde,
                                style = Stroke(width = 4f)
                            )

                            drawCircle(
                                color = EditorVerde,
                                radius = 5f,
                                center = inicio
                            )

                            drawCircle(
                                color = EditorVerde,
                                radius = 5f,
                                center = fin
                            )
                        }
                    }

                    // DIBUJAR LOS COMPONENTES

                    componentes.forEach { componente ->

                        val x = componente.x * escala
                        val y = componente.y * escala

                        val color = when (componente.tipo) {
                            TipoComponenteEditor.ENTRADA,
                            TipoComponenteEditor.RELOJ -> EditorAzul

                            TipoComponenteEditor.LED ->
                                if (resultado.getOrNull()
                                        ?.leds
                                        ?.get(componente.id) == true
                                ) EditorVerde else Color.DarkGray

                            else -> EditorNaranja
                        }

                        if (componente.tipo == TipoComponenteEditor.LED) {

                            drawCircle(
                                color = color,
                                radius = 23f * escala,
                                center = Offset(
                                    x + 56f * escala,
                                    y + 34f * escala
                                )
                            )

                            drawCircle(
                                color = Color.White,
                                radius = 23f * escala,
                                center = Offset(
                                    x + 56f * escala,
                                    y + 34f * escala
                                ),
                                style = Stroke(width = 2f)
                            )

                        } else {

                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, y),
                                size = Size(
                                    ANCHO_COMPONENTE * escala,
                                    ALTO_COMPONENTE * escala
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                    12f * escala
                                )
                            )

                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(x, y),
                                size = Size(
                                    ANCHO_COMPONENTE * escala,
                                    ALTO_COMPONENTE * escala
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                    12f * escala
                                ),
                                style = Stroke(width = 2f)
                            )
                        }

                        val texto = when (componente.tipo) {
                            TipoComponenteEditor.ENTRADA ->
                                "IN ${if (componente.valor) 1 else 0}"

                            TipoComponenteEditor.RELOJ ->
                                "CLK ${if (componente.valor) 1 else 0}"

                            TipoComponenteEditor.FLIP_FLOP_JK -> "FLIP JK"
                            TipoComponenteEditor.FLIP_FLOP_D -> "FLIP D"
                            TipoComponenteEditor.LED ->
                                if (resultado.getOrNull()
                                        ?.leds
                                        ?.get(componente.id) == true
                                ) "ON" else "OFF"

                            else -> componente.tipo.name
                        }

                        drawContext.canvas.nativeCanvas.drawText(
                            texto,
                            x + 56f * escala,
                            y + 29f * escala,
                            android.graphics.Paint().apply {
                                this.color = android.graphics.Color.WHITE
                                textSize = 12f * escala
                                textAlign =
                                    android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                                isAntiAlias = true
                            }
                        )

                        drawContext.canvas.nativeCanvas.drawText(
                            componente.id,
                            x + 56f * escala,
                            y + 48f * escala,
                            android.graphics.Paint().apply {
                                this.color = android.graphics.Color.WHITE
                                textSize = 10f * escala
                                textAlign =
                                    android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                        )

                        // Terminales de entrada.
                        if (componente.tipo.cantidadEntradas == 1) {
                            drawCircle(
                                Color.White,
                                6f * escala,
                                Offset(
                                    x,
                                    y + 34f * escala
                                )
                            )
                        }

                        if (componente.tipo.cantidadEntradas == 2) {
                            drawCircle(
                                Color.White,
                                6f * escala,
                                Offset(x, y + 22f * escala)
                            )

                            drawCircle(
                                Color.White,
                                6f * escala,
                                Offset(x, y + 46f * escala)
                            )
                        }

                        // Terminal de salida.
                        if (componente.tipo != TipoComponenteEditor.LED) {
                            drawCircle(
                                EditorVerde,
                                7f * escala,
                                Offset(
                                    x + ANCHO_COMPONENTE * escala,
                                    y + 34f * escala
                                )
                            )
                        }
                    }
                }

                Text(
                    "Azul: fuentes | Naranja: compuertas | Verde: cables y salidas",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )

                if (origenPendiente.isNotEmpty()) {
                    Text(
                        "Conectando desde $origenPendiente: toca una terminal blanca.",
                        color = EditorVerde
                    )
                }
            }
        }

        // CONTROLES DE ENTRADAS

        Card(
            colors = CardDefaults.cardColors(
                containerColor = EditorPanel
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "CONTROL DE ENTRADAS",
                    color = EditorNaranja,
                    fontWeight = FontWeight.Bold
                )

                componentes.filter {
                    it.tipo == TipoComponenteEditor.ENTRADA ||
                        it.tipo == TipoComponenteEditor.RELOJ
                }.forEach { componente ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${componente.id} - ${componente.tipo.nombre}: ${
                                if (componente.valor) 1 else 0
                            }",
                            color = Color.White
                        )

                        Button(
                            onClick = { cambiarValor(componente.id) }
                        ) {
                            Text("CAMBIAR")
                        }
                    }
                }
            }
        }

        // SIMULACIÓN

        Card(
            colors = CardDefaults.cardColors(
                containerColor = EditorPanel
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    "SIMULACIÓN DIGITAL",
                    color = EditorNaranja,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        runCatching {
                            val nuevos = MotorEditorCircuitos.aplicarPulso(
                                componentes.toList(),
                                conexiones.toList()
                            )

                            componentes.clear()
                            componentes.addAll(nuevos)

                            mensaje = "Pulso aplicado."
                        }.onFailure {
                            mensaje = it.message ?: "Error de simulación."
                        }
                    },
                    enabled = componentes.any {
                        it.tipo == TipoComponenteEditor.FLIP_FLOP_D ||
                            it.tipo == TipoComponenteEditor.FLIP_FLOP_JK
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("APLICAR PULSO DE RELOJ")
                }

                Button(
                    onClick = {
                        componentes.indices.forEach { indice ->
                            val actual = componentes[indice]

                            if (
                                actual.tipo == TipoComponenteEditor.FLIP_FLOP_D ||
                                actual.tipo == TipoComponenteEditor.FLIP_FLOP_JK
                            ) {
                                componentes[indice] =
                                    actual.copy(estado = false)
                            }
                        }

                        mensaje = "Flip-Flops reiniciados."
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("REINICIAR MEMORIA")
                }

                Button(
                    onClick = {
                        componentes.clear()
                        conexiones.clear()
                        origenPendiente = ""
                        mensaje = "Mesa de trabajo limpiada."
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C3838)
                    )
                ) {
                    Text("LIMPIAR CIRCUITO")
                }

                Text(
                    "ESTADOS INTERNOS DE LOS FLIP-FLOPS",
                    color = EditorAzul,
                    fontWeight = FontWeight.Bold
                )

                componentes.filter {
                    it.tipo == TipoComponenteEditor.FLIP_FLOP_D ||
                        it.tipo == TipoComponenteEditor.FLIP_FLOP_JK
                }.forEach { flipFlop ->
                    Text(
                        "${flipFlop.id} (${flipFlop.tipo.nombre}): Q = ${if (flipFlop.estado) 1 else 0} | Q complementaria = ${if (flipFlop.estado) 0 else 1}",
                        color = EditorVerde
                    )
                }
                Text(
                    resultado.getOrNull()?.leds?.entries
                        ?.joinToString("\n") {
                            "${it.key}: ${
                                if (it.value) "ENCENDIDO" else "APAGADO"
                            }"
                        } ?: "No hay resultados disponibles.",
                    color = EditorVerde
                )

                Text(
                    mensaje,
                    color = Color.White
                )
            }
        }

        // LISTA PARA ELIMINAR COMPONENTES

        Card(
            colors = CardDefaults.cardColors(
                containerColor = EditorPanel
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    "COMPONENTES DEL CIRCUITO",
                    color = EditorNaranja,
                    fontWeight = FontWeight.Bold
                )

                componentes.forEach { componente ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${componente.id} - ${componente.tipo.nombre}",
                            color = Color.White
                        )

                        TextButton(
                            onClick = {
                                componentes.removeAll {
                                    it.id == componente.id
                                }

                                conexiones.removeAll {
                                    it.origenId == componente.id ||
                                        it.destinoId == componente.id
                                }

                                if (origenPendiente == componente.id) {
                                    origenPendiente = ""
                                }

                                mensaje = "Componente eliminado."
                            }
                        ) {
                            Text(
                                "ELIMINAR",
                                color = EditorNaranja
                            )
                        }
                    }
                }
            }
        }
    }
}



