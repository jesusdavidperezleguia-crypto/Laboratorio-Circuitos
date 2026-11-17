package com.example.laboratoriocircuitos

import com.example.laboratoriocircuitos.logic.TipoComponenteEditor

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.laboratoriocircuitos.logic.ComponenteEditor
import com.example.laboratoriocircuitos.logic.ConexionEditor

object GeneradorPDFSecuencial {

    private const val ANCHO = 595
    private const val ALTO = 842
    private const val MARGEN = 45f
    private const val ANCHO_UTIL = ANCHO - MARGEN * 2

    private val tinta = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(40, 48, 60)
        textSize = 10f
    }

    private val titulo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(220, 100, 35)
        textSize = 18f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val seccion = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(35, 105, 75)
        textSize = 13f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val negrita = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(40, 48, 60)
        textSize = 10f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val linea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(190, 198, 207)
        strokeWidth = 0.8f
    }

    private val fondoTabla = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(235, 240, 244)
    }

    fun generar(
        nombreGuia: String,
        textoGuia: String,
        contador6: Int,
        contador9: Int,
        contador7490: Int,
        salida: OutputStream,
        estudiante: String = "",
        docente: String = "",
        componentes: List<ComponenteEditor> = emptyList(),
        conexiones: List<ConexionEditor> = emptyList()
    ) {
        val documento = PdfDocument()
        val informe = Informe(documento)

        try {
            val fecha = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

            // PORTADA
            informe.portada(
                estudiante = estudiante,
                docente = docente,
                fecha = fecha,
                guia = nombreGuia
            )

            // CONTENIDO
            informe.tituloSeccion("1. OBJETIVO GENERAL")
            informe.parrafo(
                "Estudiar el funcionamiento de los circuitos secuenciales mediante " +
                "el análisis de señales de reloj, contadores digitales y elementos de memoria."
            )

            informe.tituloSeccion("2. OBJETIVOS ESPECÍFICOS")
            informe.lista(listOf(
                "Comprender la función del temporizador 555 como generador de pulsos.",
                "Identificar el funcionamiento del contador integrado 74LS90 o 7490.",
                "Analizar contadores módulo 6 y módulo 9.",
                "Interpretar estados binarios y secuencias de conteo.",
                "Comprobar el comportamiento lógico mediante simulación."
            ))

            informe.tituloSeccion("3. MATERIALES DE LA GUÍA")
            informe.lista(listOf(
                "Protoboard y cables de conexión UTP.",
                "Temporizador integrado LM555.",
                "Resistencias y condensador.",
                "Contador integrado TTL 7490.",
                "Fuente de alimentación de 5 V.",
                "Diodos LED para visualizar los estados."
            ))

            informe.tituloSeccion("4. MARCO TEÓRICO")
            informe.subtitulo("4.1 Circuitos secuenciales")
            informe.parrafo(
                "Un circuito secuencial es un sistema digital cuya salida depende " +
                "de las entradas actuales y del estado almacenado anteriormente. " +
                "Utiliza elementos de memoria para conservar información."
            )

            informe.subtitulo("4.2 Flip-flop")
            informe.parrafo(
                "El flip-flop es un elemento de memoria capaz de almacenar un bit. " +
                "La combinación de varios flip-flops permite construir contadores " +
                "y otros sistemas digitales secuenciales."
            )

            informe.subtitulo("4.3 Temporizador 555")
            informe.parrafo(
                "El temporizador 555 puede configurarse en modo astable para producir " +
                "una señal periódica. Esta señal se utiliza como reloj para controlar " +
                "el avance de los contadores."
            )

            informe.subtitulo("4.4 Contador 7490")
            informe.parrafo(
                "El integrado 7490 es un contador decimal asíncrono de cuatro bits. " +
                "Contiene una sección divisora por dos y otra divisora por cinco. " +
                "Su configuración permite obtener una secuencia de diez estados."
            )

            informe.tituloSeccion("5. DESARROLLO DE LA PRÁCTICA")
            informe.subtitulo("5.1 Contador módulo 6")
            informe.parrafo(
                "Este contador recorre seis estados, desde cero hasta cinco. " +
                "Utiliza tres bits identificados como C, B y A. Después del estado " +
                "cinco regresa al estado cero."
            )
            informe.tablaContador(6, 3, "C", "B", "A")

            informe.subtitulo("5.2 Contador módulo 9")
            informe.parrafo(
                "Este contador recorre nueve estados, desde cero hasta ocho. " +
                "Necesita cuatro bits D, C, B y A, porque tres bits solamente " +
                "permiten representar ocho combinaciones."
            )
            informe.tablaContador(9, 4, "D", "C", "B", "A")

            informe.subtitulo("5.3 Contador decimal 74LS90")
            informe.parrafo(
                "El contador decimal recorre diez estados, desde cero hasta nueve, " +
                "y utiliza cuatro bits para representar cada estado."
            )
            informe.tablaContador(10, 4, "D", "C", "B", "A")

            informe.tituloSeccion("6. PROCEDIMIENTO EXPERIMENTAL")
            informe.lista(listOf(
                "Identificar los componentes electrónicos indicados en la guía.",
                "Preparar el circuito temporizador 555 en configuración astable.",
                "Utilizar la señal de reloj para activar el contador.",
                "Conectar las salidas del contador a los indicadores LED.",
                "Observar los cambios de estado y registrar las combinaciones binarias.",
                "Comparar la secuencia observada con la secuencia teórica."
            ))

            informe.tituloSeccion("7. RESULTADOS DE LA SIMULACIÓN")
            informe.parrafo(
                "Los valores siguientes corresponden al estado actual de los " +
                "simuladores de la aplicación al momento de generar el informe. " +
                "No representan mediciones de un montaje físico."
            )

            informe.subtitulo("7.1 Estados internos de los Flip-Flops")

            val flipFlopsInforme = componentes.filter {
                it.tipo == TipoComponenteEditor.FLIP_FLOP_D ||
                    it.tipo == TipoComponenteEditor.FLIP_FLOP_JK
            }

            if (flipFlopsInforme.isEmpty()) {
                informe.parrafo(
                    "No se registraron Flip-Flops D o JK en el editor " +
                    "al momento de generar este informe."
                )
            } else {
                informe.parrafo(
                    "Se registran los estados internos de los elementos " +
                    "de memoria presentes en el circuito diseñado."
                )

                informe.lista(
                    flipFlopsInforme.map { flipFlop ->
                        "${flipFlop.id} - ${flipFlop.tipo.nombre}: " +
                        "Q = ${if (flipFlop.estado) 1 else 0}; " +
                        "Q complementaria = ${if (flipFlop.estado) 0 else 1}."
                    }
                )
            }

            informe.parrafo(
                "El Flip-Flop D almacena el valor de su entrada D al aplicar " +
                "un pulso. El Flip-Flop JK conserva, reinicia, activa o " +
                "invierte su estado según sus entradas J y K. Los valores " +
                "registrados corresponden a la simulación lógica de la aplicación."
            )
            informe.tablaResultados(
                listOf(
                    Triple(
                        "Módulo 6",
                        contador6.coerceIn(0, 5),
                        3
                    ),
                    Triple(
                        "Módulo 9",
                        contador9.coerceIn(0, 8),
                        4
                    ),
                    Triple(
                        "Contador 7490",
                        contador7490.coerceIn(0, 9),
                        4
                    )
                )
            )

            informe.tituloSeccion("8. ANÁLISIS DE LAS SITUACIONES PROBLÉMICAS")

            informe.subtitulo("8.1 ¿Cuál es la configuración interna del 7490?")
            informe.parrafo(
                "El 7490 está compuesto por dos secciones de conteo: una sección " +
                "divisora por dos y otra divisora por cinco. La combinación de ambas " +
                "permite obtener una secuencia decimal de diez estados. Las conexiones " +
                "externas determinan su configuración de funcionamiento."
            )

            informe.subtitulo("8.2 ¿Qué sucede cuando llegan pulsos al pin 14?")
            informe.parrafo(
                "El pin 14 corresponde a la entrada de reloj CP0. Los pulsos aplicados " +
                "a esta entrada hacen avanzar la sección divisora por dos. El comportamiento " +
                "del conjunto depende de las conexiones entre las secciones del integrado."
            )

            informe.subtitulo("8.3 ¿Cómo diseñar un contador de 0 a 8?")
            informe.parrafo(
                "Se requieren cuatro flip-flops porque cuatro bits permiten representar " +
                "los nueve estados comprendidos entre 0000 y 1000. Tres bits no son " +
                "suficientes. El diseño debe incorporar una lógica de control que " +
                "detecte el estado de reinicio y permita repetir la secuencia."
            )

            informe.tituloSeccion("9. DIAGRAMA DEL CIRCUITO DISEÑADO")
            informe.parrafo(
                "La siguiente representación corresponde al circuito elaborado en el editor virtual de la aplicación. Se muestran los componentes y las conexiones registradas al momento de generar este informe."
            )
            informe.diagrama(componentes, conexiones)

            informe.tituloSeccion("10. CONCLUSIONES")
            informe.lista(listOf(
                "Los circuitos secuenciales almacenan estados y realizan operaciones que dependen del tiempo.",
                "Los pulsos de reloj controlan los cambios de estado de los contadores.",
                "La cantidad de bits depende del número de estados que se desea representar.",
                "El contador módulo 6 utiliza tres bits y el módulo 9 necesita cuatro bits.",
                "La simulación permite estudiar el comportamiento lógico antes de realizar un montaje físico."
            ))

            informe.tituloSeccion("11. REFERENCIAS BIBLIOGRÁFICAS")
            informe.parrafo(
                "Tocci, R. J., Widmer, N. S. y Moss, G. L. " +
                "Sistemas Digitales: Principios y Aplicaciones. 12.ª edición. " +
                "Pearson, México, 2017."
            )
            informe.parrafo(
                "Floyd, T. L. y Del Pino, S. " +
                "Fundamentos de Sistemas Digitales. 11.ª edición. " +
                "Pearson, Madrid, 2016."
            )

            informe.tituloSeccion("12. INFORMACIÓN DE LA GUÍA ORIGINAL")
            informe.parrafo("Archivo: ${nombreGuia.ifBlank { "No especificado" }}")

            informe.parrafo(
                textoGuia.ifBlank {
                    "No se encontró texto extraído de la guía."
                }.take(6000)
            )

            informe.finalizar()
            documento.writeTo(salida)
        } finally {
            documento.close()
        }
    }

    private class Informe(
        private val documento: PdfDocument
    ) {
        private var pagina: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var numeroPagina = 0
        private var y = 75f

        private fun nuevaPagina() {
            cerrarPagina()
            numeroPagina++

            val info = PdfDocument.PageInfo.Builder(
                ANCHO,
                ALTO,
                numeroPagina
            ).create()

            pagina = documento.startPage(info)
            canvas = pagina!!.canvas

            canvas!!.drawText(
                "LABORATORIO DE CIRCUITOS",
                MARGEN,
                35f,
                titulo
            )

            canvas!!.drawLine(
                MARGEN,
                48f,
                ANCHO - MARGEN,
                48f,
                linea
            )

            y = 76f
        }

        fun portada(
            estudiante: String,
            docente: String,
            fecha: String,
            guia: String
        ) {
            nuevaPagina()

            val c = canvas ?: return
            val centrado = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(40, 48, 60)
                textSize = 15f
                textAlign = Paint.Align.CENTER
            }

            val grande = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(220, 100, 35)
                textSize = 25f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
            }

            val universidad = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(35, 105, 75)
                textSize = 19f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
            }

            c.drawText(
                "UNIVERSIDAD DE CARTAGENA",
                ANCHO / 2f,
                180f,
                universidad
            )

            c.drawText(
                "INFORME DE LABORATORIO",
                ANCHO / 2f,
                310f,
                grande
            )

            c.drawText(
                "CIRCUITOS SECUENCIALES",
                ANCHO / 2f,
                355f,
                centrado
            )

            c.drawText(
                "Asignatura: Sistemas Digitales",
                ANCHO / 2f,
                410f,
                centrado
            )

            val datos = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(40, 48, 60)
                textSize = 12f
            }

            var datoY = 525f
            val datosTexto = listOf(
                "Presentado por: ${estudiante.ifBlank { "No especificado" }}",
                "Docente: ${docente.ifBlank { "No especificado" }}",
                "Fecha: $fecha",
                "Guía: ${guia.ifBlank { "No especificada" }}"
            )

            datosTexto.forEach {
                val fragmentos = dividir(it, datos, ANCHO_UTIL)
                fragmentos.forEach { fragmento ->
                    c.drawText(fragmento, MARGEN + 35f, datoY, datos)
                    datoY += 20f
                }
                datoY += 8f
            }

            val ciudad = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(35, 105, 75)
                textSize = 12f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

            c.drawText(
                "CARTAGENA DE INDIAS",
                ANCHO / 2f,
                760f,
                ciudad
            )

            cerrarPagina()
        }

        fun tituloSeccion(texto: String) {
            espacio(34f)
            asegurarEspacio(45f)

            val c = canvas ?: return
            c.drawText(texto, MARGEN, y, seccion)
            y += 10f

            c.drawLine(
                MARGEN,
                y,
                ANCHO - MARGEN,
                y,
                linea
            )
            y += 17f
        }

        fun subtitulo(texto: String) {
            espacio(8f)
            escribirTexto(texto, negrita, 16f)
        }

        fun parrafo(texto: String) {
            espacio(5f)
            escribirTexto(texto, tinta, 15f)
            espacio(7f)
        }

        fun lista(elementos: List<String>) {
            elementos.forEach {
                escribirTexto("• $it", tinta, 15f)
                espacio(3f)
            }
            espacio(7f)
        }

        fun tablaContador(
            modulo: Int,
            bits: Int,
            vararg nombres: String
        ) {
            val encabezados = listOf("Decimal") + nombres.toList()
            tabla(encabezados) {
                (0 until modulo).map { estado ->
                    val binario = estado.toString(2).padStart(bits, '0')
                    listOf(estado.toString()) + binario.map { it.toString() }
                }
            }

            parrafo(
                "Estado siguiente al último valor: 0. " +
                "La secuencia vuelve al inicio del conteo."
            )
        }

        fun tablaResultados(
            resultados: List<Triple<String, Int, Int>>
        ) {
            tabla(
                listOf("Simulador", "Decimal", "Binario")
            ) {
                resultados.map {
                    listOf(
                        it.first,
                        it.second.toString(),
                        it.second.toString(2).padStart(it.third, '0')
                    )
                }
            }
        }

        private fun tabla(
            encabezados: List<String>,
            filas: () -> List<List<String>>
        ) {
            val cantidad = encabezados.size
            val anchoColumna = ANCHO_UTIL / cantidad
            val altoFila = 23f

            asegurarEspacio((filas().size + 1) * altoFila + 12f)

            val c = canvas ?: return
            var filaY = y

            c.drawRect(
                MARGEN,
                filaY - 15f,
                ANCHO - MARGEN,
                filaY + 7f,
                fondoTabla
            )

            encabezados.forEachIndexed { indice, texto ->
                c.drawText(
                    texto,
                    MARGEN + indice * anchoColumna + 7f,
                    filaY,
                    negrita
                )
            }

            filaY += altoFila

            filas().forEach { fila ->
                fila.forEachIndexed { indice, valor ->
                    c.drawText(
                        valor,
                        MARGEN + indice * anchoColumna + 7f,
                        filaY,
                        tinta
                    )
                }

                c.drawLine(
                    MARGEN,
                    filaY + 5f,
                    ANCHO - MARGEN,
                    filaY + 5f,
                    linea
                )

                filaY += altoFila
            }

            y = filaY + 12f
        }

        fun diagrama(
            componentes: List<ComponenteEditor>,
            conexiones: List<ConexionEditor>
        ) {
            if (componentes.isEmpty()) {
                parrafo(
                    "No se diseñó ningún circuito en el editor virtual. " +
                    "Por esta razón, el informe no incluye un diagrama de componentes."
                )
                return
            }

            val anchoComponente = 112f
            val altoComponente = 68f

            val minimoX = componentes.minOf { it.x }
            val minimoY = componentes.minOf { it.y }
            val maximoX = componentes.maxOf { it.x } + anchoComponente
            val maximoY = componentes.maxOf { it.y } + altoComponente

            val rangoX = (maximoX - minimoX).coerceAtLeast(1f)
            val rangoY = (maximoY - minimoY).coerceAtLeast(1f)

            val escala = minOf(
                (ANCHO_UTIL - 30f) / rangoX,
                270f / rangoY
            ).coerceAtLeast(0.15f)

            val anchoDibujo = rangoX * escala
            val altoDibujo = rangoY * escala

            asegurarEspacio(altoDibujo + 75f)

            val c = canvas ?: return
            val inicioX = MARGEN + (ANCHO_UTIL - anchoDibujo) / 2f
            val inicioY = y + 20f

            fun convertirX(valor: Float): Float {
                return inicioX + (valor - minimoX) * escala
            }

            fun convertirY(valor: Float): Float {
                return inicioY + (valor - minimoY) * escala
            }

            val cable = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(35, 145, 100)
                style = Paint.Style.STROKE
                strokeWidth = 1.8f
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }

            val terminal = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(35, 145, 100)
                style = Paint.Style.FILL
            }

            val borde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }

            val relleno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
            }

            val textoComponente = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = (8f * escala).coerceIn(5f, 8f)
                typeface = Typeface.DEFAULT_BOLD
            }

            val textoId = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(220, 225, 235)
                textAlign = Paint.Align.CENTER
                textSize = (6f * escala).coerceIn(4f, 6f)
            }

            val componentesPorId = componentes.associateBy { it.id }

            // Dibujar primero los cables para que queden detrás de los componentes.
            conexiones.forEach { conexion ->
                val origen = componentesPorId[conexion.origenId]
                val destino = componentesPorId[conexion.destinoId]

                if (origen != null && destino != null) {
                    val salidaX = convertirX(origen.x + anchoComponente)
                    val salidaY = convertirY(origen.y + 34f)

                    val entradaYLocal = when (destino.tipo.cantidadEntradas) {
                        1 -> 34f
                        2 -> if (conexion.entradaDestino == 0) 22f else 46f
                        else -> 34f
                    }

                    val entradaX = convertirX(destino.x)
                    val entradaY = convertirY(destino.y + entradaYLocal)
                    val medioX = (salidaX + entradaX) / 2f

                    c.drawLine(salidaX, salidaY, medioX, salidaY, cable)
                    c.drawLine(medioX, salidaY, medioX, entradaY, cable)
                    c.drawLine(medioX, entradaY, entradaX, entradaY, cable)

                    c.drawCircle(salidaX, salidaY, 2.4f, terminal)
                    c.drawCircle(entradaX, entradaY, 2.4f, terminal)
                }
            }

            // Dibujar los componentes encima de los cables.
            componentes.forEach { componente ->
                val x = convertirX(componente.x)
                val componenteY = convertirY(componente.y)
                val ancho = anchoComponente * escala
                val alto = altoComponente * escala

                val colorComponente = when (componente.tipo.name) {
                    "ENTRADA", "RELOJ" -> Color.rgb(55, 115, 185)
                    "LED" -> if (componente.estado) {
                        Color.rgb(35, 145, 90)
                    } else {
                        Color.rgb(85, 95, 110)
                    }
                    else -> Color.rgb(225, 115, 45)
                }

                relleno.color = colorComponente
                borde.color = colorComponente

                if (componente.tipo.name == "LED") {
                    c.drawCircle(
                        x + ancho / 2f,
                        componenteY + alto / 2f,
                        minOf(ancho, alto) * 0.34f,
                        relleno
                    )
                    c.drawCircle(
                        x + ancho / 2f,
                        componenteY + alto / 2f,
                        minOf(ancho, alto) * 0.34f,
                        borde.apply {
                            color = Color.WHITE
                            strokeWidth = 1.2f
                        }
                    )
                    borde.color = colorComponente
                } else {
                    c.drawRoundRect(
                        x,
                        componenteY,
                        x + ancho,
                        componenteY + alto,
                        5f,
                        5f,
                        relleno
                    )
                    c.drawRoundRect(
                        x,
                        componenteY,
                        x + ancho,
                        componenteY + alto,
                        5f,
                        5f,
                        borde.apply {
                            color = Color.WHITE
                            strokeWidth = 0.8f
                        }
                    )
                    borde.color = colorComponente
                }

                val nombre = when (componente.tipo.name) {
                    "ENTRADA" -> "IN ${if (componente.valor) 1 else 0}"
                    "RELOJ" -> "CLK ${if (componente.valor) 1 else 0}"
                    "FLIP_FLOP_JK" -> "FLIP JK"
                    "FLIP_FLOP_D" -> "FLIP D"
                    "LED" -> if (componente.estado) "ON" else "OFF"
                    else -> componente.tipo.nombre
                }

                c.drawText(
                    nombre,
                    x + ancho / 2f,
                    componenteY + alto / 2f + 3f,
                    textoComponente
                )

                c.drawText(
                    componente.id,
                    x + ancho / 2f,
                    componenteY + alto - 5f * escala.coerceAtLeast(0.6f),
                    textoId
                )

                // Terminales de entrada.
                when (componente.tipo.cantidadEntradas) {
                    1 -> c.drawCircle(
                        x,
                        componenteY + alto / 2f,
                        2f,
                        terminal
                    )
                    2 -> {
                        c.drawCircle(x, componenteY + 22f * escala, 2f, terminal)
                        c.drawCircle(x, componenteY + 46f * escala, 2f, terminal)
                    }
                }

                // Terminal de salida.
                if (componente.tipo.name != "LED") {
                    c.drawCircle(
                        x + ancho,
                        componenteY + alto / 2f,
                        2.3f,
                        terminal
                    )
                }
            }

            y = inicioY + altoDibujo + 25f

            // Inventario de componentes del circuito.
            escribirTexto("COMPONENTES DEL CIRCUITO", negrita, 15f)

            componentes.forEach { componente ->
                val detalle = "${componente.id}: ${componente.tipo.nombre}"
                escribirTexto("• $detalle", tinta, 13f)
            }

            espacio(5f)
            escribirTexto("CONEXIONES DEL CIRCUITO", negrita, 15f)

            if (conexiones.isEmpty()) {
                escribirTexto("No se registraron conexiones.", tinta, 13f)
            } else {
                conexiones.forEach { conexion ->
                    val origen = componentesPorId[conexion.origenId]
                    val destino = componentesPorId[conexion.destinoId]

                    val nombreEntrada = when (conexion.entradaDestino) {
                        0 -> "entrada 1"
                        1 -> "entrada 2"
                        else -> "entrada ${conexion.entradaDestino + 1}"
                    }

                    val detalle = "${conexion.origenId} → ${conexion.destinoId} ($nombreEntrada)"
                    escribirTexto("• $detalle", tinta, 13f)
                }
            }

            espacio(8f)
        }

        private fun escribirTexto(
            texto: String,
            pintura: Paint,
            salto: Float
        ) {
            texto.split("\n").forEach { lineaTexto ->
                val fragmentos = dividir(lineaTexto, pintura, ANCHO_UTIL)

                fragmentos.forEach { fragmento ->
                    asegurarEspacio(salto + 3f)

                    // Obtener siempre el lienzo de la página actual.
                    // Esto evita escribir sobre una página que ya fue cerrada.
                    canvas?.drawText(
                        fragmento,
                        MARGEN,
                        y,
                        pintura
                    )

                    y += salto
                }
            }
        }
        private fun dividir(
            texto: String,
            pintura: Paint,
            ancho: Float
        ): List<String> {
            val palabras = texto.split(Regex("\\s+"))
                .filter { it.isNotBlank() }

            if (palabras.isEmpty()) return listOf("")

            val resultado = mutableListOf<String>()
            var actual = ""

            palabras.forEach { palabra ->
                val prueba = if (actual.isEmpty()) palabra else "$actual $palabra"

                if (pintura.measureText(prueba) > ancho && actual.isNotEmpty()) {
                    resultado.add(actual)
                    actual = palabra
                } else {
                    actual = prueba
                }
            }

            if (actual.isNotEmpty()) resultado.add(actual)
            return resultado
        }

        private fun espacio(valor: Float) {
            y += valor
        }

        private fun asegurarEspacio(alto: Float) {
            if (pagina == null) nuevaPagina()

            if (y + alto > ALTO - 55f) {
                nuevaPagina()
            }
        }

        fun finalizar() {
            cerrarPagina()
        }

        private fun cerrarPagina() {
            val actual = pagina ?: return

            canvas?.drawLine(
                MARGEN,
                ALTO - 42f,
                ANCHO - MARGEN,
                ALTO - 42f,
                linea
            )

            val pie = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.GRAY
                textSize = 8f
            }

            canvas?.drawText(
                "LaboratorioCircuitos - Página $numeroPagina",
                MARGEN,
                ALTO - 25f,
                pie
            )

            documento.finishPage(actual)
            pagina = null
            canvas = null
        }
    }
}



