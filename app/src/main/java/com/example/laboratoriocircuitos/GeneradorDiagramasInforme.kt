package com.example.laboratoriocircuitos

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.NodoCircuitoDigital

object GeneradorDiagramasInforme {

    private const val ANCHO = 595
    private const val ALTO = 842

    private val naranja = Color.rgb(220, 100, 35)
    private val oscuro = Color.rgb(35, 45, 60)
    private val azul = Color.rgb(55, 115, 190)
    private val verde = Color.rgb(45, 145, 95)
    private val gris = Color.rgb(115, 130, 150)

    private fun pintura(
        tamano: Float = 12f,
        color: Int = oscuro,
        negrita: Boolean = false
    ) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = tamano
        this.color = color
        typeface = Typeface.create(
            Typeface.DEFAULT,
            if (negrita) Typeface.BOLD else Typeface.NORMAL
        )
    }

    private fun textoCentrado(
        canvas: Canvas,
        texto: String,
        centroX: Float,
        y: Float,
        paint: Paint
    ) {
        canvas.drawText(
            texto,
            centroX - paint.measureText(texto) / 2f,
            y,
            paint
        )
    }

    private fun nuevaPagina(
        documento: PdfDocument,
        numero: Int,
        titulo: String
    ): PdfDocument.Page {
        val info = PdfDocument.PageInfo.Builder(
            ANCHO,
            ALTO,
            numero
        ).create()

        val pagina = documento.startPage(info)
        val canvas = pagina.canvas

        canvas.drawColor(Color.WHITE)

        canvas.drawText(
            "LABORATORIO CIRCUITOS",
            48f,
            38f,
            pintura(17f, naranja, true)
        )

        canvas.drawLine(
            48f, 52f, 547f, 52f,
            pintura(1f, gris)
        )

        canvas.drawText(
            titulo,
            48f,
            88f,
            pintura(17f, oscuro, true)
        )

        return pagina
    }
    private fun finalizarPagina(
        documento: PdfDocument,
        pagina: PdfDocument.Page,
        numero: Int
    ) {
        val canvas = pagina.canvas

        canvas.drawLine(
            48f, 805f, 547f, 805f,
            pintura(1f, gris)
        )

        canvas.drawText(
            "Laboratorio Circuitos - Página $numero",
            48f,
            824f,
            pintura(9f, gris)
        )

        documento.finishPage(pagina)
    }
    fun agregarDiagramasBooleanos(
        documento: PdfDocument,
        paginaInicial: Int
    ): Int {
        dibujarBooleano(documento, paginaInicial, "AL = D + !E", "D + !E")
        dibujarBooleano(documento, paginaInicial + 1, "AP = A * B * C * !D * E * F", "A*B*C*!D*E*F")
        dibujarBooleano(documento, paginaInicial + 2, "R = A * B", "A*B")
        return 3
    }

    fun agregarDiagramasSecuenciales(
        documento: PdfDocument,
        paginaInicial: Int,
        datos: DatosSecuencialesInforme
    ): Int {
        dibujarContadores(documento, paginaInicial, datos)

        if (datos.componentes.isNotEmpty()) {
            dibujarEditor(documento, paginaInicial + 1, datos)
            return 2
        }

        return 1
    }

    fun agregarPaginas(
        documento: PdfDocument,
        paginaInicial: Int,
        datos: DatosSecuencialesInforme
    ) {
        val cantidadBooleanos = agregarDiagramasBooleanos(documento, paginaInicial)
        agregarDiagramasSecuenciales(
            documento,
            paginaInicial + cantidadBooleanos,
            datos
        )
    }
    private fun dibujarBooleano(
        documento: PdfDocument,
        numero: Int,
        formula: String,
        expresion: String
    ) {
        val pagina = nuevaPagina(
            documento,
            numero,
            "DIAGRAMA DE ÁLGEBRA BOOLEANA"
        )
        val canvas = pagina.canvas

        canvas.drawText(
            "Función: $formula",
            48f,
            125f,
            pintura(13f, naranja, true)
        )

        val raiz = GeneradorCircuitos.generar(
            expresion
        ).raiz

        val cantidadHojas = contarHojas(raiz).coerceAtLeast(1)
        val espacio = 490f / cantidadHojas

        dibujarArbol(
            canvas = canvas,
            nodo = raiz,
            inicioHoja = 0,
            profundidad = 0,
            espacio = espacio,
            cantidadHojas = cantidadHojas
        )

        canvas.drawText(
            "Representación lógica generada automáticamente a partir de la expresión.",
            48f,
            755f,
            pintura(10f, gris)
        )

        finalizarPagina(documento, pagina, numero)
    }

    private fun contarHojas(
        nodo: NodoCircuitoDigital
    ): Int {
        return when (nodo) {
            is NodoCircuitoDigital.Variable,
            is NodoCircuitoDigital.Constante -> 1

            is NodoCircuitoDigital.CompuertaNOT ->
                contarHojas(nodo.entrada)

            is NodoCircuitoDigital.CompuertaAND ->
                contarHojas(nodo.entradaA) +
                    contarHojas(nodo.entradaB)

            is NodoCircuitoDigital.CompuertaOR ->
                contarHojas(nodo.entradaA) +
                    contarHojas(nodo.entradaB)

            is NodoCircuitoDigital.CompuertaNAND ->
                contarHojas(nodo.entradaA) +
                    contarHojas(nodo.entradaB)

            is NodoCircuitoDigital.CompuertaNOR ->
                contarHojas(nodo.entradaA) +
                    contarHojas(nodo.entradaB)
        }
    }

    private fun etiqueta(
        nodo: NodoCircuitoDigital
    ): String {
        return when (nodo) {
            is NodoCircuitoDigital.Variable -> nodo.nombre
            is NodoCircuitoDigital.Constante ->
                if (nodo.valor) "1" else "0"

            is NodoCircuitoDigital.CompuertaNOT -> "NOT"
            is NodoCircuitoDigital.CompuertaAND -> "AND"
            is NodoCircuitoDigital.CompuertaOR -> "OR"
            is NodoCircuitoDigital.CompuertaNAND -> "NAND"
            is NodoCircuitoDigital.CompuertaNOR -> "NOR"
        }
    }

    private fun dibujarArbol(
        canvas: Canvas,
        nodo: NodoCircuitoDigital,
        inicioHoja: Int,
        profundidad: Int,
        espacio: Float,
        cantidadHojas: Int
    ) {
        val hojas = contarHojas(nodo)
        val centroX = 52f +
            (inicioHoja + hojas / 2f) * espacio

        val y = 155f + profundidad * 100f
        val esHoja = nodo is NodoCircuitoDigital.Variable ||
            nodo is NodoCircuitoDigital.Constante

        val anchoCaja = if (esHoja) 66f else 78f
        val altoCaja = 38f

        val hijos = when (nodo) {
            is NodoCircuitoDigital.CompuertaNOT ->
                listOf(nodo.entrada)

            is NodoCircuitoDigital.CompuertaAND ->
                listOf(nodo.entradaA, nodo.entradaB)

            is NodoCircuitoDigital.CompuertaOR ->
                listOf(nodo.entradaA, nodo.entradaB)

            is NodoCircuitoDigital.CompuertaNAND ->
                listOf(nodo.entradaA, nodo.entradaB)

            is NodoCircuitoDigital.CompuertaNOR ->
                listOf(nodo.entradaA, nodo.entradaB)

            else -> emptyList()
        }

        var siguienteHoja = inicioHoja

        hijos.forEach { hijo ->
            val hojasHijo = contarHojas(hijo)
            val centroHijo = 52f +
                (siguienteHoja + hojasHijo / 2f) * espacio

            canvas.drawLine(
                centroX,
                y + altoCaja,
                centroHijo,
                y + 100f,
                pintura(2f, azul)
            )

            dibujarArbol(
                canvas,
                hijo,
                siguienteHoja,
                profundidad + 1,
                espacio,
                cantidadHojas
            )

            siguienteHoja += hojasHijo
        }

        val rect = RectF(
            centroX - anchoCaja / 2f,
            y,
            centroX + anchoCaja / 2f,
            y + altoCaja
        )

        val colorCaja = if (esHoja) azul else naranja

        canvas.drawRoundRect(
            rect,
            9f,
            9f,
            pintura(2f, colorCaja).apply {
                style = Paint.Style.STROKE
            }
        )

        textoCentrado(
            canvas,
            etiqueta(nodo),
            centroX,
            y + 25f,
            pintura(12f, oscuro, true)
        )
    }

    private fun dibujarContadores(
        documento: PdfDocument,
        numero: Int,
        datos: DatosSecuencialesInforme
    ) {
        val pagina = nuevaPagina(
            documento,
            numero,
            "DIAGRAMA FUNCIONAL DE CIRCUITOS SECUENCIALES"
        )
        val canvas = pagina.canvas

        canvas.drawText(
            "Esquema funcional de los contadores implementados",
            48f,
            125f,
            pintura(12f, oscuro)
        )

        val centroX = 297f
        val reloj = RectF(205f, 165f, 390f, 225f)

        canvas.drawRoundRect(
            reloj,
            10f,
            10f,
            pintura(2f, azul).apply {
                style = Paint.Style.STROKE
            }
        )

        textoCentrado(
            canvas,
            "RELOJ / PULSOS",
            centroX,
            190f,
            pintura(14f, azul, true)
        )

        textoCentrado(
            canvas,
            "Señal de entrada",
            centroX,
            210f,
            pintura(10f, gris)
        )

        val bloques = listOf(
            Triple("CONTADOR MOD-6", "Valor: ${datos.contador6}", 300f),
            Triple("CONTADOR MOD-9", "Valor: ${datos.contador9}", 450f),
            Triple("CONTADOR 7490", "Valor: ${datos.contador7490}", 600f)
        )

        bloques.forEach { (nombre, valor, y) ->
            canvas.drawLine(
                centroX,
                225f,
                centroX,
                y - 35f,
                pintura(2f, azul)
            )

            val rect = RectF(
                150f,
                y - 30f,
                445f,
                y + 30f
            )

            canvas.drawRoundRect(
                rect,
                10f,
                10f,
                pintura(2f, naranja).apply {
                    style = Paint.Style.STROKE
                }
            )

            textoCentrado(
                canvas,
                nombre,
                centroX,
                y - 3f,
                pintura(13f, naranja, true)
            )

            textoCentrado(
                canvas,
                valor,
                centroX,
                y + 17f,
                pintura(11f, oscuro)
            )

            canvas.drawLine(
                centroX,
                y + 30f,
                centroX,
                y + 55f,
                pintura(2f, verde)
            )

            textoCentrado(
                canvas,
                "SALIDA / ESTADO",
                centroX,
                y + 75f,
                pintura(10f, verde, true)
            )
        }

        canvas.drawText(
            "Nota: esquema funcional educativo; no representa el cableado de pines de un montaje físico.",
            48f,
            755f,
            pintura(10f, gris)
        )

        finalizarPagina(documento, pagina, numero)
    }

    private fun dibujarEditor(
        documento: PdfDocument,
        numero: Int,
        datos: DatosSecuencialesInforme
    ) {
        val pagina = nuevaPagina(
            documento,
            numero,
            "CIRCUITO DEL EDITOR Y SUS CONEXIONES"
        )
        val canvas = pagina.canvas

        canvas.drawText(
            "Componentes y conexiones registrados en el editor",
            48f,
            125f,
            pintura(12f, oscuro)
        )

        val componentes = datos.componentes
        val porId = componentes.associateBy { it.id }

        val anchoComponente = 112f
        val altoComponente = 68f

        val minX = componentes.minOf { it.x }
        val minY = componentes.minOf { it.y }
        val maxX = componentes.maxOf { it.x } + anchoComponente
        val maxY = componentes.maxOf { it.y } + altoComponente

        val escalaX = 480f / (maxX - minX).coerceAtLeast(1f)
        val escalaY = 520f / (maxY - minY).coerceAtLeast(1f)
        val escala = minOf(escalaX, escalaY, 1f).coerceAtLeast(0.25f)

        canvas.save()
        canvas.translate(55f, 175f)
        canvas.scale(escala, escala)
        canvas.translate(-minX, -minY)

        datos.conexiones.forEach { conexion ->
            val origen = porId[conexion.origenId]
            val destino = porId[conexion.destinoId]

            if (origen != null && destino != null) {
                val x1 = origen.x + anchoComponente
                val y1 = origen.y + altoComponente / 2f
                val x2 = destino.x
                val y2 = destino.y + altoComponente / 2f

                val medioX = (x1 + x2) / 2f

                val cable = pintura(2f, azul)

                canvas.drawLine(x1, y1, medioX, y1, cable)
                canvas.drawLine(medioX, y1, medioX, y2, cable)
                canvas.drawLine(medioX, y2, x2, y2, cable)
            }
        }

        componentes.forEach { componente ->
            val color = when (componente.tipo.nombre.uppercase()) {
                "ENTRADA", "RELOJ" -> azul
                "LED" -> verde
                else -> naranja
            }

            val rect = RectF(
                componente.x,
                componente.y,
                componente.x + anchoComponente,
                componente.y + altoComponente
            )

            canvas.drawRoundRect(
                rect,
                9f,
                9f,
                pintura(2f, color).apply {
                    style = Paint.Style.STROKE
                }
            )

            canvas.drawText(
                componente.tipo.nombre.take(17),
                componente.x + 7f,
                componente.y + 25f,
                pintura(12f, oscuro, true)
            )

            canvas.drawText(
                componente.id.take(18),
                componente.x + 7f,
                componente.y + 43f,
                pintura(10f, gris)
            )

            canvas.drawText(
                "Estado: ${if (componente.estado) 1 else 0}",
                componente.x + 7f,
                componente.y + 58f,
                pintura(9f, oscuro)
            )
        }

        canvas.restore()

        canvas.drawText(
            "Componentes: ${datos.componentes.size}",
            48f,
            735f,
            pintura(11f, oscuro, true)
        )

        canvas.drawText(
            "Conexiones: ${datos.conexiones.size}",
            48f,
            755f,
            pintura(11f, oscuro, true)
        )

        finalizarPagina(documento, pagina, numero)
    }
}


