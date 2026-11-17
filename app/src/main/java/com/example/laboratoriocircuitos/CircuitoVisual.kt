package com.example.laboratoriocircuitos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.laboratoriocircuitos.logic.NodoCircuitoDigital

@Composable
fun CircuitoVisual(raiz: NodoCircuitoDigital) {

    val naranja = Color(0xFFFF8733)
    val azul = Color(0xFF64B5F6)
    val verde = Color(0xFF70D69A)
    val blanco = Color.White

    data class Elemento(
        val texto: String,
        val x: Float,
        val y: Float,
        val color: Color
    )

    val elementos = mutableListOf<Elemento>()
    val conexiones = mutableListOf<Pair<Offset, Offset>>()

    var contadorY = 0

    fun construir(
        nodo: NodoCircuitoDigital,
        profundidad: Int
    ): Offset {

        val x = 100f + profundidad * 190f

        return when (nodo) {

            is NodoCircuitoDigital.Variable -> {
                val y = 90f + contadorY * 100f
                contadorY++

                elementos.add(
                    Elemento(nodo.nombre, x, y, azul)
                )

                Offset(x, y)
            }

            is NodoCircuitoDigital.Constante -> {
                val y = 90f + contadorY * 100f
                contadorY++

                elementos.add(
                    Elemento(
                        if (nodo.valor) "1" else "0",
                        x,
                        y,
                        azul
                    )
                )

                Offset(x, y)
            }

            is NodoCircuitoDigital.CompuertaNOT -> {
                val entrada = construir(nodo.entrada, profundidad + 1)
                val y = entrada.y

                elementos.add(
                    Elemento("NOT", x, y, naranja)
                )

                conexiones.add(
                    Offset(x + 45f, y) to entrada
                )

                Offset(x, y)
            }

            is NodoCircuitoDigital.CompuertaAND -> {
                val a = construir(nodo.entradaA, profundidad + 1)
                val b = construir(nodo.entradaB, profundidad + 1)
                val y = (a.y + b.y) / 2f

                elementos.add(
                    Elemento("AND", x, y, naranja)
                )

                conexiones.add(Offset(x + 45f, y) to a)
                conexiones.add(Offset(x + 45f, y) to b)

                Offset(x, y)
            }

            is NodoCircuitoDigital.CompuertaOR -> {
                val a = construir(nodo.entradaA, profundidad + 1)
                val b = construir(nodo.entradaB, profundidad + 1)
                val y = (a.y + b.y) / 2f

                elementos.add(
                    Elemento("OR", x, y, naranja)
                )

                conexiones.add(Offset(x + 45f, y) to a)
                conexiones.add(Offset(x + 45f, y) to b)

                Offset(x, y)
            }
        }
    }

    val salida = construir(raiz, 0)

    Column {

        Text(
            text = "DIAGRAMA DEL CIRCUITO",
            color = naranja,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            )
        ) {

            Canvas(
                modifier = Modifier.size(
                    width = (contadorY.coerceAtLeast(2) * 110 + 250).dp,
                    height = (contadorY.coerceAtLeast(2) * 100 + 80).dp
                )
            ) {

                val escala = density

                conexiones.forEach { (origen, destino) ->

                    drawLine(
                        color = verde,
                        start = Offset(
                            (origen.x + 45f) * escala,
                            origen.y * escala
                        ),
                        end = Offset(
                            destino.x * escala,
                            destino.y * escala
                        ),
                        strokeWidth = 3f
                    )
                }

                elementos.forEach { elemento ->

                    val centro = Offset(
                        elemento.x * escala,
                        elemento.y * escala
                    )

                    drawCircle(
                        color = elemento.color,
                        radius = 31f * escala,
                        center = centro
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        elemento.texto,
                        centro.x,
                        centro.y + 6f * escala,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 13f * escala
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }

                drawContext.canvas.nativeCanvas.drawText(
                    "Z",
                    (salida.x - 55f) * escala,
                    salida.y * escala + 6f * escala,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 20f * escala
                        isFakeBoldText = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Naranja: compuertas   Azul: entradas   Verde: conexiones",
            color = blanco,
            style = MaterialTheme.typography.bodySmall
        )
    }
}