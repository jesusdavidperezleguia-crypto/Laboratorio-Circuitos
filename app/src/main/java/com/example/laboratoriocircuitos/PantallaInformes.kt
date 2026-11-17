package com.example.laboratoriocircuitos

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val InformeNaranja = Color(0xFFFF8733)
private val InformePanel = Color(0xFF1C293B)
private val InformeTexto = Color(0xFFF5F7FA)
private val InformeSecundario = Color(0xFFB8C4D6)
private val InformeVerde = Color(0xFF70D69A)

@Composable
fun PantallaInformes(datosTCC: DatosInformeTCC? = null, datosSecuenciales: DatosSecuencialesInforme = DatosSecuencialesInforme()) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("Informe de laboratorio de circuitos digitales") }
    var asignatura by remember { mutableStateOf("Sistemas Digitales") }
    var objetivo by remember { mutableStateOf("Desarrollar y analizar un sistema digital de control de acceso industrial mediante funciones booleanas, tablas de verdad y mapas de Karnaugh.") }
    var procedimiento by remember { mutableStateOf("Se identificaron las entradas digitales, los sensores y las salidas del sistema. Se definieron las funciones booleanas, se generaron tablas de verdad, se simplificaron las expresiones mediante mapas de Karnaugh y se compararon las funciones originales con las simplificadas.") }
    var resultados by remember { mutableStateOf("La aplicacion calcula las funciones de alarma, apertura y registro. Los resultados detallados, mapas de Karnaugh y tablas de verdad se incorporan automaticamente en este documento.") }
    var conclusiones by remember { mutableStateOf("El desarrollo permite modelar las condiciones principales de un sistema industrial de control de acceso. La equivalencia entre las funciones originales y simplificadas se comprueba mediante tablas de verdad. La aplicacion integra simulaciones de contadores y circuitos secuenciales con flip-flops D y JK. Los resultados permiten observar estados y transiciones logicas mediante pulsos de reloj simulados.") }
    var mensaje by remember { mutableStateOf("") }
    var datosAutomaticos by remember { mutableStateOf(construirResumenCompletoTCC()) }

    val tituloActual by rememberUpdatedState(titulo)
    val asignaturaActual by rememberUpdatedState(asignatura)
    val objetivoActual by rememberUpdatedState(objetivo)
    val procedimientoActual by rememberUpdatedState(procedimiento)
    val resultadosActual by rememberUpdatedState(resultados)
    val conclusionesActual by rememberUpdatedState(conclusiones)
    val datosAutomaticosActual by rememberUpdatedState(datosAutomaticos)

    val selectorPDF = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            try {
                val fecha = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date())

                val contenido = """
                    ${tituloActual.uppercase()}

                    Asignatura: $asignaturaActual
                    Fecha de elaboración: $fecha

                    1. OBJETIVO
                    ${objetivoActual.ifBlank { "No especificado." }}

                    2. PROCEDIMIENTO Y DESARROLLO
                    ${procedimientoActual.ifBlank { "No especificado." }}

                    3. RESULTADOS
                    ${resultadosActual.ifBlank { "No especificados." }}

                    4. RESULTADOS TÉCNICOS AUTOMÁTICOS
                    ${datosAutomaticosActual.ifBlank { "No se incorporaron resultados de Karnaugh." }}

                    FASE 4. CIRCUITOS SECUENCIALES Y MEMORIA

                    CONTADORES IMPLEMENTADOS
                    Contador modulo 6: ${datosSecuenciales.contador6}
                    Contador modulo 9: ${datosSecuenciales.contador9}
                    Contador 7490: ${datosSecuenciales.contador7490}

                    COMPONENTES DEL EDITOR
                    ${datosSecuenciales.componentes.joinToString("\n") { componente ->
                        "${componente.id}: ${componente.tipo.nombre} | Estado: ${if (componente.estado) 1 else 0}"
                    }.ifBlank { "No se registraron componentes en el editor." }}

                    CONEXIONES DEL CIRCUITO
                    ${datosSecuenciales.conexiones.joinToString("\n") { conexion ->
                        "${conexion.origenId} -> ${conexion.destinoId} (entrada ${conexion.entradaDestino})"
                    }.ifBlank { "No se registraron conexiones en el editor." }}

                    ESTADOS DE LOS FLIP-FLOPS
                    ${datosSecuenciales.componentes.filter {
                        it.tipo == com.example.laboratoriocircuitos.logic.TipoComponenteEditor.FLIP_FLOP_D ||
                        it.tipo == com.example.laboratoriocircuitos.logic.TipoComponenteEditor.FLIP_FLOP_JK
                    }.joinToString("\n") { flipFlop ->
                        "${flipFlop.id} (${flipFlop.tipo.nombre}): Q = ${if (flipFlop.estado) 1 else 0}; Q complementaria = ${if (flipFlop.estado) 0 else 1}"
                    }.ifBlank { "No hay flip-flops registrados en el editor." }}

                    La simulación de los flip-flops D y JK permite observar el almacenamiento y la actualización de estados mediante pulsos de reloj. Los resultados corresponden al modelo lógico educativo de la aplicación, no a mediciones de hardware físico.

                    5. VALIDACIÓN GENERAL
                    Las expresiones originales y simplificadas se comparan mediante sus tablas de verdad. La equivalencia se verifica cuando ambas producen la misma salida para todas las entradas.

                    6. ALCANCE Y LIMITACIONES
                    El conteo de complejidad es una estimación de nodos lógicos y no representa un conteo físico definitivo de compuertas. La salida R representa una condición lógica de registro validado, no almacenamiento persistente de eventos. La integración con sensores y actuadores físicos no está implementada en esta versión.

                    7. CONCLUSIONES
                    ${conclusionesActual.ifBlank { "No especificadas." }}

                    DOCUMENTO GENERADO CON LABORATORIO CIRCUITOS
                    Proyecto educativo de sistemas digitales.
                """.trimIndent()

                context.contentResolver.openOutputStream(uri)?.use { salida ->
                    generarDocumentoPDF(contenido, salida)
                } ?: throw IllegalStateException("No se pudo abrir el archivo.")

                mensaje = "PDF generado correctamente."
            } catch (e: Exception) {
                mensaje = "No se pudo generar el PDF: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Text(
            text = "INFORMES Y DOCUMENTACIÓN",
            color = InformeNaranja,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Redacta el informe de tus prácticas, actividades y proyectos.",
            color = InformeSecundario,
            fontSize = 13.sp
        )

        if (datosTCC != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = InformePanel
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text(
                        text = "RESULTADOS DE KARNAUGH DISPONIBLES",
                        color = InformeVerde,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Fórmula: ${datosTCC.formulaOriginal}",
                        color = InformeTexto
                    )

                    Text(
                        text = "Simplificada: ${datosTCC.resultadoKarnaugh.formulaSimplificada}",
                        color = InformeTexto
                    )

                    Text(
                        text = "Mínterminos: ${datosTCC.resultadoKarnaugh.minterminos.size}",
                        color = InformeSecundario
                    )

                    Button(
                        onClick = {
                            datosAutomaticos = construirResumenCompletoTCC()
                            mensaje = "Resultados de Karnaugh incorporados al informe."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InformeNaranja
                        )
                    ) {
                        Text(
                            "INCORPORAR RESULTADOS AL INFORME",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Todavía no hay resultados de Karnaugh. Abre ese módulo, analiza una fórmula y vuelve aquí.",
                color = InformeSecundario,
                fontSize = 12.sp
            )
        }

        CampoInforme("Título del informe", titulo) { titulo = it }
        CampoInforme("Asignatura", asignatura) { asignatura = it }

        CampoInforme(
            etiqueta = "Objetivo de la actividad",
            valor = objetivo,
            lineas = 4
        ) { objetivo = it }

        CampoInforme(
            etiqueta = "Procedimiento y desarrollo",
            valor = procedimiento,
            lineas = 6
        ) { procedimiento = it }

        CampoInforme(
            etiqueta = "Resultados obtenidos",
            valor = resultados,
            lineas = 5
        ) { resultados = it }

        CampoInforme(
            etiqueta = "Conclusiones",
            valor = conclusiones,
            lineas = 5
        ) { conclusiones = it }

        Button(
            onClick = {
                val nombre = titulo
                    .trim()
                    .replace(Regex("[^A-Za-z0-9_-]"), "_")
                    .take(45)
                    .ifBlank { "Informe_Laboratorio" }

                selectorPDF.launch("$nombre.pdf")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = InformeNaranja
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "GENERAR Y GUARDAR PDF",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        if (mensaje.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = InformePanel
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(14.dp),
                    color = InformeVerde,
                    fontSize = 13.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = InformePanel
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "ESTRUCTURA DEL DOCUMENTO",
                    color = InformeNaranja,
                    fontWeight = FontWeight.Bold
                )
                Text("• Título y asignatura", color = InformeTexto)
                Text("• Objetivo", color = InformeTexto)
                Text("• Procedimiento", color = InformeTexto)
                Text("• Resultados", color = InformeTexto)
                Text("• Conclusiones", color = InformeTexto)
            }
        }
    }
}

private fun construirResumenTCC(datos: DatosInformeTCC): String {
    val k = datos.resultadoKarnaugh
    val tabla = datos.tablaVerdad
    val comparacion = datos.comparacion

    return buildString {
        appendLine("ANÁLISIS DE ÁLGEBRA BOOLEANA Y KARNAUGH")
        appendLine()
        appendLine("1. FUNCIÓN ORIGINAL")
        appendLine(datos.formulaOriginal)
        appendLine()
        appendLine("2. VARIABLES")
        appendLine(k.variables.joinToString(", "))
        appendLine()
        appendLine("3. MINTERMINOS")
        appendLine(
            k.minterminos.joinToString(", ").ifBlank { "No se encontraron minterminos." }
        )
        appendLine()
        appendLine("4. MAPA DE KARNAUGH")
        appendLine("Filas Gray: ${k.filasGray.joinToString(" | ")}")
        appendLine("Columnas Gray: ${k.columnasGray.joinToString(" | ")}")
        appendLine()

        k.mapa.forEachIndexed { indice, fila ->
            val valoresLogicos = fila.map { mintermino ->
                if (mintermino in k.minterminos) "1" else "0"
            }
            appendLine(
                "${k.filasGray[indice]} | ${valoresLogicos.joinToString(" | ")}"
            )
        }

        appendLine()
        appendLine("5. AGRUPACIONES")
        if (k.grupos.isEmpty()) {
            appendLine("No se registraron agrupaciones.")
        } else {
            k.grupos.forEachIndexed { indice, grupo ->
                appendLine("Grupo ${indice + 1}:")
                appendLine("Patrón: ${grupo.patron}")
                appendLine("Expresión: ${grupo.expresion}")
                appendLine("Minterminos: ${grupo.minterminos.joinToString(", ")}")
                appendLine("Celdas: ${grupo.celdas.joinToString(", ")}")
                appendLine()
            }
        }

        appendLine("6. FUNCIÓN SIMPLIFICADA")
        appendLine(k.formulaSimplificada)
        appendLine()
        appendLine("7. COMPARACIÓN DE COMPLEJIDAD")
        appendLine("Nodos estimados originales: ${comparacion.compuertasOriginales}")
        appendLine("Nodos estimados simplificados: ${comparacion.compuertasSimplificadas}")
        appendLine("Nodos reducidos: ${comparacion.compuertasReducidas}")
        appendLine("Reducción estimada: ${"%.2f".format(Locale.US, comparacion.porcentajeReduccion)} %")
        appendLine("Equivalencia: ${if (comparacion.equivalentes) "Verificada" else "No verificada"}")
        appendLine(comparacion.mensaje)
        appendLine("Nota: la comparación representa una estimación de nodos lógicos de las expresiones, no un conteo físico definitivo de compuertas.")
        appendLine()
        appendLine("8. TABLA DE VERDAD")
        appendLine(
            (tabla.variables + "SALIDA").joinToString(" | ")
        )

        tabla.filas.forEach { fila ->
            val valores = tabla.variables.map { variable ->
                if (fila.entradas[variable] == true) "1" else "0"
            }
            appendLine(
                (valores + if (fila.salida) "1" else "0").joinToString(" | ")
            )
        }

        appendLine()
        appendLine("9. SIMULACIÓN")
        datos.entradasSimulacion.toSortedMap().forEach { (variable, valor) ->
            appendLine("$variable = ${if (valor) 1 else 0}")
        }
        appendLine("Salida original: ${if (datos.salidaOriginal) 1 else 0}")
        appendLine("Salida simplificada: ${if (datos.salidaSimplificada) 1 else 0}")
        appendLine(
            "Coincidencia: ${
                if (datos.salidaOriginal == datos.salidaSimplificada) "Sí" else "No"
            }"
        )
    }
}
@Composable
private fun CampoInforme(
    etiqueta: String,
    valor: String,
    lineas: Int = 1,
    cambiar: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = cambiar,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        minLines = lineas,
        maxLines = if (lineas == 1) 1 else lineas + 2,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InformeNaranja,
            unfocusedBorderColor = InformeSecundario,
            focusedLabelColor = InformeNaranja,
            unfocusedLabelColor = InformeSecundario,
            focusedTextColor = InformeTexto,
            unfocusedTextColor = InformeTexto,
            cursorColor = InformeNaranja
        )
    )
}

private fun generarDocumentoPDF(
    contenido: String,
    salida: java.io.OutputStream
) {
    val documento = PdfDocument()

    val ancho = 595
    val alto = 842
    val margen = 48f

    val pintura = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(35, 45, 60)
        textSize = 11f
    }

    val pinturaTitulo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(220, 100, 35)
        textSize = 17f
        typeface = android.graphics.Typeface.create(
            android.graphics.Typeface.DEFAULT,
            android.graphics.Typeface.BOLD
        )
    }

    val lineas = mutableListOf<String>()

    contenido.lines().forEach { linea ->
        if (linea.isBlank()) {
            lineas.add("")
        } else {
            var restante = linea
            while (restante.isNotEmpty()) {
                var corte = restante.length
                while (
                    corte > 1 &&
                    pintura.measureText(restante.substring(0, corte)) > ancho - margen * 2
                ) {
                    corte--
                }

                if (corte < restante.length) {
                    val espacio = restante.lastIndexOf(' ', corte)
                    if (espacio > 0) corte = espacio
                }

                lineas.add(restante.substring(0, corte).trimEnd())
                restante = restante.substring(corte).trimStart()
            }
        }
    }

    val lineasPorPagina = 55
    val grupos = lineas.chunked(lineasPorPagina)

    try {
        grupos.forEachIndexed { indice, grupo ->
            val informacion = PdfDocument.PageInfo.Builder(
                ancho,
                alto,
                indice + 1
            ).create()

            val pagina = documento.startPage(informacion)
            val canvas = pagina.canvas

            canvas.drawText(
                "LABORATORIO CIRCUITOS",
                margen,
                38f,
                pinturaTitulo
            )

            canvas.drawLine(
                margen,
                52f,
                ancho - margen,
                52f,
                pintura
            )

            var y = 80f

            grupo.forEachIndexed { posicion, linea ->
                if (linea.isNotBlank()) {
                    if (indice == 0 && posicion == 0) {
                        canvas.drawText(linea.take(65), margen, y, pinturaTitulo)
                    } else {
                        canvas.drawText(linea, margen, y, pintura)
                    }
                }
                y += 13.5f
            }

            canvas.drawText(
                "Página ${indice + 1} de ${grupos.size}",
                margen,
                alto - 25f,
                pintura
            )

            documento.finishPage(pagina)
        }

        documento.writeTo(salida)
    } finally {
        documento.close()
    }
}
