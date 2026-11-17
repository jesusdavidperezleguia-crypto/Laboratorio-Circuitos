package com.example.laboratoriocircuitos

import android.graphics.Paint
import android.graphics.Path
import android.graphics.pdf.PdfDocument
import com.example.laboratoriocircuitos.logic.*
import java.io.OutputStream
import kotlin.math.max

object GeneradorPDFActividad {

    private const val ANCHO = 595
    private const val ALTO = 842
    private const val MARGEN = 42f

    private val texto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(40, 48, 60)
        textSize = 10f
    }

    private val titulo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(220, 100, 35)
        textSize = 17f
        typeface = android.graphics.Typeface.create(
            android.graphics.Typeface.DEFAULT,
            android.graphics.Typeface.BOLD
        )
    }

    private val subtitulo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(35, 105, 75)
        textSize = 12f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private var numeroPagina = 0
    private lateinit var documento: PdfDocument

    fun generar(
        solucion: ResultadoSolucionEjercicio,
        salida: OutputStream
    ) {
        documento = PdfDocument()
        numeroPagina = 0

        try {
            paginaTexto(
                listOf(
                    "INFORME DE ACTIVIDAD",
                    "",
                    "LABORATORIO CIRCUITOS",
                    "Informe académico de circuitos digitales",
                    "",
                    "Asignatura: Sistemas Digitales",
                    "Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())}",
                    "",
                    "1. FORMULACIÓN DEL PROBLEMA",
                    "Fórmula original: ${solucion.formulaOriginal}",
                    "Fórmula normalizada: ${solucion.formulaNormalizada}",
                    "Variables: ${solucion.variables.joinToString(", ")}",
                    "",
                    "2. OBJETIVO",
                    "Analizar una función booleana, construir su circuito lógico,",
                    "obtener su tabla de verdad y comprobar su simplificación.",
                    "",
                    "3. DESARROLLO",
                    "Se generó el circuito original a partir de la expresión.",
                    "Posteriormente se evaluaron todas las combinaciones posibles",
                    "de las variables y se aplicó el proceso de simplificación.",
                    "",
                    "4. RESULTADO DE LA SIMULACIÓN",
                    solucion.mensaje,
                    "Cantidad de compuertas originales: ${solucion.circuito.cantidadCompuertas}",
                    "Cantidad de compuertas simplificadas: ${solucion.circuitoSimplificado.cantidadCompuertas}",
                    "",
                    "5. EXPRESIÓN SIMPLIFICADA",
                    solucion.simplificacion.simplificada,
                    "",
                    "6. PASOS DE SIMPLIFICACIÓN"
                ) + solucion.simplificacion.pasos.flatMapIndexed { i, paso ->
                    listOf(
                        "Paso ${i + 1}: ${paso.regla}",
                        "${paso.antes} -> ${paso.despues}",
                        paso.explicacion,
                        ""
                    )
                } + listOf(
                    "7. VERIFICACIÓN",
                    "Tabla de verdad: ${
                        if (solucion.verificacionTablaCorrecta) "CORRECTA" else "REVISAR"
                    }",
                    "Equivalencia: ${
                        if (solucion.verificacionEquivalenciaCorrecta) "CONFIRMADA" else "NO CONFIRMADA"
                    }",
                    "",
                    "8. CONCLUSIÓN",
                    if (
                        solucion.verificacionTablaCorrecta &&
                        solucion.verificacionEquivalenciaCorrecta
                    ) {
                        "La simulación coincide con la tabla de verdad y la expresión simplificada conserva el comportamiento lógico de la expresión original."
                    } else {
                        "Los resultados requieren revisión. No se confirmó completamente la correspondencia entre la tabla de verdad y las expresiones."
                    },
                    "",
                    "Documento generado con LaboratorioCircuitos."
                )
            )

            generarPaginasTabla(solucion)
            generarPaginaCircuito(solucion.circuito, "CIRCUITO ORIGINAL")
            generarPaginaCircuito(solucion.circuitoSimplificado, "CIRCUITO SIMPLIFICADO")

            // 9. LABORATORIO BOOLEANO
            val entradasLaboratorio = solucion.circuito.variables.associateWith {
                false
            }

            val simulacionLaboratorio = SimuladorCircuitos.simular(
                solucion.circuito,
                entradasLaboratorio
            )

            paginaTexto(
                listOf(
                    "9. LABORATORIO BOOLEANO",
                    "",
                    "Expresion analizada:",
                    solucion.formulaOriginal,
                    "",
                    "Variables de entrada:",
                    solucion.circuito.variables.joinToString(", "),
                    "",
                    "Cantidad de compuertas:",
                    solucion.circuito.cantidadCompuertas.toString(),
                    "",
                    "ESTADO DE LOS INTERRUPTORES",
                    "Simulacion inicial: todas las entradas apagadas (0).",
                    *entradasLaboratorio.entries.map {
                        "${it.key} = ${if (it.value) 1 else 0}"
                    }.toTypedArray(),
                    "",
                    "RESULTADO DE LA SIMULACION",
                    simulacionLaboratorio.mensaje,
                    "Salida del circuito: ${
                        if (simulacionLaboratorio.resultadoSalida) "1 (ACTIVADA)" else "0 (DESACTIVADA)"
                    }",
                    "",
                    "La simulacion anterior utiliza el estado inicial de las entradas.",
                    "La tabla siguiente presenta todas las combinaciones posibles."
                )
            )

            generarPaginasTabla(solucion)
            generarPaginaCircuito(
                solucion.circuito,
                "LABORATORIO BOOLEANO - CIRCUITO"
            )

            documento.writeTo(salida)
        } finally {
            documento.close()
        }
    }

    private fun nuevaPagina(): PdfDocument.Page {
        numeroPagina++
        val info = PdfDocument.PageInfo.Builder(
            ANCHO,
            ALTO,
            numeroPagina
        ).create()

        val pagina = documento.startPage(info)
        pagina.canvas.drawText(
            "LABORATORIO CIRCUITOS",
            MARGEN,
            35f,
            titulo
        )
        pagina.canvas.drawLine(
            MARGEN,
            48f,
            ANCHO - MARGEN,
            48f,
            texto
        )
        pagina.canvas.drawText(
            "Página $numeroPagina",
            MARGEN,
            ALTO - 22f,
            texto
        )
        return pagina
    }

    private fun paginaTexto(lineas: List<String>) {
        val preparadas = mutableListOf<String>()

        lineas.forEach { linea ->
            if (linea.isBlank()) {
                preparadas.add("")
            } else {
                var restante = linea
                while (restante.isNotEmpty()) {
                    var corte = restante.length
                    while (
                        corte > 1 &&
                        texto.measureText(restante.substring(0, corte)) > ANCHO - MARGEN * 2
                    ) {
                        corte--
                    }
                    if (corte < restante.length) {
                        val espacio = restante.lastIndexOf(' ', corte)
                        if (espacio > 0) corte = espacio
                    }
                    preparadas.add(restante.substring(0, corte).trim())
                    restante = restante.substring(corte).trim()
                }
            }
        }

        preparadas.chunked(48).forEachIndexed { indice, grupo ->
            val pagina = nuevaPagina()
            var y = 82f

            grupo.forEach { linea ->
                if (Regex("^[1-8]\\. ").containsMatchIn(linea)
                ) {
                    pagina.canvas.drawText(linea.take(85), MARGEN, y, subtitulo)
                } else {
                    pagina.canvas.drawText(linea, MARGEN, y, texto)
                }
                y += 14f
            }

            documento.finishPage(pagina)
        }
    }

    private fun generarPaginasTabla(solucion: ResultadoSolucionEjercicio) {
        val filas = solucion.tablaVerdad.filas
        val variables = solucion.tablaVerdad.variables

        filas.chunked(35).forEachIndexed { indice, grupo ->
            val pagina = nuevaPagina()
            val canvas = pagina.canvas

            canvas.drawText(
                "TABLA DE VERDAD - PARTE ${indice + 1}",
                MARGEN,
                78f,
                subtitulo
            )

            val anchoColumna = (ANCHO - MARGEN * 2) / (variables.size + 1).toFloat()
            var y = 108f

            variables.forEachIndexed { columna, variable ->
                canvas.drawText(
                    variable,
                    MARGEN + columna * anchoColumna,
                    y,
                    subtitulo
                )
            }

            canvas.drawText(
                "Z",
                MARGEN + variables.size * anchoColumna,
                y,
                subtitulo
            )

            y += 12f
            canvas.drawLine(MARGEN, y, ANCHO - MARGEN, y, texto)
            y += 18f

            grupo.forEach { fila ->
                variables.forEachIndexed { columna, variable ->
                    canvas.drawText(
                        if (fila.entradas[variable] == true) "1" else "0",
                        MARGEN + columna * anchoColumna,
                        y,
                        texto
                    )
                }

                canvas.drawText(
                    if (fila.salida) "1" else "0",
                    MARGEN + variables.size * anchoColumna,
                    y,
                    texto
                )
                y += 18f
            }

            documento.finishPage(pagina)
        }
    }

    private data class Posicion(
        val x: Float,
        val y: Float,
        val etiqueta: String,
        val hijos: List<Posicion>
    )

    private fun contarHojas(nodo: NodoCircuitoDigital): Int =
        when (nodo) {
            is NodoCircuitoDigital.Variable,
            is NodoCircuitoDigital.Constante -> 1
            is NodoCircuitoDigital.CompuertaNOT -> contarHojas(nodo.entrada)
            is NodoCircuitoDigital.CompuertaAND ->
                contarHojas(nodo.entradaA) + contarHojas(nodo.entradaB)
            is NodoCircuitoDigital.CompuertaOR ->
                contarHojas(nodo.entradaA) + contarHojas(nodo.entradaB)
        }

    private fun profundidad(nodo: NodoCircuitoDigital): Int =
        when (nodo) {
            is NodoCircuitoDigital.Variable,
            is NodoCircuitoDigital.Constante -> 0
            is NodoCircuitoDigital.CompuertaNOT ->
                1 + profundidad(nodo.entrada)
            is NodoCircuitoDigital.CompuertaAND ->
                1 + max(profundidad(nodo.entradaA), profundidad(nodo.entradaB))
            is NodoCircuitoDigital.CompuertaOR ->
                1 + max(profundidad(nodo.entradaA), profundidad(nodo.entradaB))
        }

    private fun generarPaginaCircuito(
        circuito: CircuitoGenerado,
        encabezado: String
    ) {
        val pagina = nuevaPagina()
        val canvas = pagina.canvas

        canvas.drawText(encabezado, MARGEN, 78f, subtitulo)
        canvas.drawText(
            "Expresión: ${circuito.expresion}",
            MARGEN,
            98f,
            texto
        )

        val hojas = contarHojas(circuito.raiz)
        val niveles = profundidad(circuito.raiz)
        val espacioVertical = 690f
        val pasoY = espacioVertical / max(hojas, 1)
        val pasoX = (ANCHO - 150f) / max(niveles + 1, 1)
        var indiceHoja = 0

        fun dibujar(nodo: NodoCircuitoDigital, nivel: Int): Posicion {
            val x = 75f + nivel * pasoX

            return when (nodo) {
                is NodoCircuitoDigital.Variable -> {
                    val y = 130f + (indiceHoja + 0.5f) * pasoY
                    indiceHoja++
                    Posicion(x, y, nodo.nombre, emptyList())
                }

                is NodoCircuitoDigital.Constante -> {
                    val y = 130f + (indiceHoja + 0.5f) * pasoY
                    indiceHoja++
                    Posicion(x, y, if (nodo.valor) "1" else "0", emptyList())
                }

                is NodoCircuitoDigital.CompuertaNOT -> {
                    val hijo = dibujar(nodo.entrada, nivel + 1)
                    Posicion(x, hijo.y, "NOT", listOf(hijo))
                }

                is NodoCircuitoDigital.CompuertaAND -> {
                    val a = dibujar(nodo.entradaA, nivel + 1)
                    val b = dibujar(nodo.entradaB, nivel + 1)
                    Posicion(x, (a.y + b.y) / 2f, "AND", listOf(a, b))
                }

                is NodoCircuitoDigital.CompuertaOR -> {
                    val a = dibujar(nodo.entradaA, nivel + 1)
                    val b = dibujar(nodo.entradaB, nivel + 1)
                    Posicion(x, (a.y + b.y) / 2f, "OR", listOf(a, b))
                }
            }
        }

        val raiz = dibujar(circuito.raiz, 0)

        fun pintar(posicion: Posicion) {
            posicion.hijos.forEach { hijo ->
                canvas.drawLine(
                    posicion.x + 35f,
                    posicion.y,
                    hijo.x - 35f,
                    hijo.y,
                    texto
                )
                pintar(hijo)
            }

            val caja = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(255, 135, 51)
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }

            val relleno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(245, 247, 250)
                style = Paint.Style.FILL
            }

            canvas.drawRoundRect(
                posicion.x - 31f,
                posicion.y - 17f,
                posicion.x + 31f,
                posicion.y + 17f,
                8f,
                8f,
                relleno
            )
            canvas.drawRoundRect(
                posicion.x - 31f,
                posicion.y - 17f,
                posicion.x + 31f,
                posicion.y + 17f,
                8f,
                8f,
                caja
            )

            val etiquetaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                textSize = 10f
                textAlign = Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            canvas.drawText(
                posicion.etiqueta,
                posicion.x,
                posicion.y + 4f,
                etiquetaPaint
            )
        }

        pintar(raiz)

        canvas.drawText(
            "Compuertas: ${circuito.cantidadCompuertas}",
            MARGEN,
            ALTO - 48f,
            texto
        )

        documento.finishPage(pagina)
    }
}


