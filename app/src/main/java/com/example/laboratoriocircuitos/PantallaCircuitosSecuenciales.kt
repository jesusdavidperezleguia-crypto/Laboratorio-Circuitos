package com.example.laboratoriocircuitos

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.layout.*


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.ComponenteEditor
import com.example.laboratoriocircuitos.logic.ConexionEditor

private val SecFondo = Color(0xFF101827)
private val SecPanel = Color(0xFF1C293B)
private val SecNaranja = Color(0xFFFF8733)
private val SecVerde = Color(0xFF70D69A)
private val SecTexto = Color(0xFFF5F7FA)
private val SecGris = Color(0xFFB8C4D6)

@Composable
fun PantallaCircuitosSecuenciales(
    nombreGuia: String = "",
    textoGuia: String = "",
    onDatosActualizados: (DatosSecuencialesInforme) -> Unit = {}
) {

    var contador6 by remember { mutableStateOf(0) }
    var contador9 by remember { mutableStateOf(0) }
    var contador7490 by remember { mutableStateOf(0) }
    var mostrarEditor by remember { mutableStateOf(false) }


    var componentesEditor by remember {
        mutableStateOf<List<ComponenteEditor>>(emptyList())
    }

    var conexionesEditor by remember {
        mutableStateOf<List<ConexionEditor>>(emptyList())
    }
    LaunchedEffect(
        contador6,
        contador9,
        contador7490,
        componentesEditor,
        conexionesEditor
    ) {
        onDatosActualizados(
            DatosSecuencialesInforme(
                contador6 = contador6,
                contador9 = contador9,
                contador7490 = contador7490,
                componentes = componentesEditor,
                conexiones = conexionesEditor
            )
        )
    }
    var estudiante by remember { mutableStateOf("") }
    var docente by remember { mutableStateOf("") }

    val contexto = LocalContext.current

    val crearPDF = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            try {
                val salida = contexto.contentResolver.openOutputStream(uri)

                if (salida != null) {
                    salida.use {
                        GeneradorPDFSecuencial.generar(
                            nombreGuia = nombreGuia,
                            textoGuia = textoGuia,
                            contador6 = contador6,
                            contador9 = contador9,
                            contador7490 = contador7490,
                            salida = it,
                            estudiante = estudiante,
                            docente = docente,
                            componentes = componentesEditor,
                            conexiones = conexionesEditor
                        )
                    }

                    Toast.makeText(
                        contexto,
                        "Informe PDF guardado correctamente.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        contexto,
                        "No se pudo crear el archivo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (error: Exception) {
                Toast.makeText(
                    contexto,
                    "Error al generar PDF: ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Button(
            onClick = { mostrarEditor = !mostrarEditor },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (mostrarEditor) "CERRAR EDITOR VIRTUAL"
                else "ABRIR EDITOR VIRTUAL"
            )
        }

        if (mostrarEditor) {
            PantallaEditorCircuitos(
                onCircuitoChange = { nuevosComponentes, nuevasConexiones ->
                    componentesEditor = nuevosComponentes
                    conexionesEditor = nuevasConexiones
                }
            )
        }

        Text(
            text = "LABORATORIO DE CIRCUITOS SECUENCIALES",
            color = SecNaranja,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Estudio y simulación de contadores digitales, señales de reloj y circuitos con memoria.",
            color = SecGris,
            fontSize = 14.sp
        )

        TarjetaSecuencial(
            titulo = "1. TEMPORIZADOR 555",
            descripcion = "El circuito integrado 555 puede configurarse como astable para generar una señal periódica de reloj. Esta señal permite que los contadores cambien de estado.",
            contenido = """
                FUNCIONAMIENTO:

                • Genera pulsos eléctricos periódicos.
                • Cada pulso puede activar el avance de un contador.
                • La frecuencia determina la rapidez de los cambios.
                • El período es el tiempo que tarda un ciclo completo.

                En esta aplicación se utilizan pulsos manuales para representar el reloj de manera educativa.
            """.trimIndent()
        )

        TarjetaSecuencial(
            titulo = "2. CONTADOR 74LS90 / 7490",
            descripcion = "El 7490 es un contador decimal asíncrono de cuatro bits. Internamente dispone de una sección divisora por 2 y otra por 5.",
            contenido = """
                ENTRADA DE RELOJ:

                • El pin 14 corresponde a la entrada CP0.
                • Recibe los pulsos de reloj para la sección divisora por 2.
                • Los cambios de estado se propagan por el circuito.
                • Su configuración permite obtener conteo decimal de 0 a 9.

                IMPORTANTE:
                La simulación representa el conteo lógico. No sustituye el montaje ni las mediciones eléctricas del circuito físico.
            """.trimIndent()
        )

        TarjetaContadorSecuencial(
            titulo = "3. CONTADOR DE 0 A 5",
            descripcion = "Contador módulo 6. Utiliza tres bits: C, B y A. Después del estado 5 vuelve a cero.",
            valor = contador6,
            bits = 3,
            modulo = 6,
            nombresBits = "C  B  A",
            onPulso = { contador6 = (contador6 + 1) % 6 },
            onReiniciar = { contador6 = 0 }
        )

        TarjetaContadorSecuencial(
            titulo = "4. CONTADOR DE 0 A 8 CON FLIP-FLOPS",
            descripcion = "Contador módulo 9. Necesita cuatro bits porque el número 8 se representa como 1000.",
            valor = contador9,
            bits = 4,
            modulo = 9,
            nombresBits = "D  C  B  A",
            onPulso = { contador9 = (contador9 + 1) % 9 },
            onReiniciar = { contador9 = 0 }
        )

        TarjetaContadorSecuencial(
            titulo = "5. SIMULACIÓN DEL 74LS90",
            descripcion = "Representación del conteo decimal de un contador década de cuatro bits.",
            valor = contador7490,
            bits = 4,
            modulo = 10,
            nombresBits = "D  C  B  A",
            onPulso = { contador7490 = (contador7490 + 1) % 10 },
            onReiniciar = { contador7490 = 0 }
        )

        TarjetaSecuencial(
            titulo = "6. DIFERENCIA ENTRE CIRCUITOS",
            descripcion = "Un circuito secuencial conserva información de su estado anterior.",
            contenido = """
                CIRCUITO COMBINACIONAL:
                La salida depende de las entradas actuales.

                CIRCUITO SECUENCIAL:
                La salida depende de las entradas y del estado almacenado.

                FLIP-FLOP:
                Es un elemento de memoria capaz de almacenar un bit.

                RELOJ:
                Sincroniza los cambios de estado de los sistemas digitales.
            """.trimIndent()
        )

        TarjetaSecuencial(
            titulo = "7. SITUACIONES PROBLÉMICAS",
            descripcion = "Preguntas de análisis de la guía de laboratorio.",
            contenido = """
                1. Investigar la configuración interna del circuito integrado 7490.

                2. ¿Qué sucede cuando llegan pulsos de reloj al pin 14 del 7490?

                3. Diseñar un contador de 0 a 8 empleando flip-flops. Se requieren cuatro bits para representar los nueve estados.

                4. Elaborar conclusiones sobre el funcionamiento de los circuitos secuenciales.
            """.trimIndent()
        )

        TarjetaSecuencial(
            titulo = "8. REFERENCIAS BIBLIOGRÁFICAS",
            descripcion = "Bibliografía indicada en la guía.",
            contenido = """
                • Tocci, R. J. y colaboradores. Sistemas Digitales: Principios y Aplicaciones. 12.ª edición, 2017.

                • Floyd, T. L. Fundamentos de Sistemas Digitales. 11.ª edición, 2016.
            """.trimIndent()
        )

        Text(
            text = "DATOS DEL INFORME",
            color = SecNaranja,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = estudiante,
            onValueChange = { estudiante = it },
            label = { Text("Nombre del estudiante") },
            placeholder = { Text("Escribe tu nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = docente,
            onValueChange = { docente = it },
            label = { Text("Nombre del docente") },
            placeholder = { Text("Escribe el nombre del docente") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = if (nombreGuia.isBlank() || textoGuia.isBlank())
                "Primero carga una guía PDF en la sección ACTIVIDADES para habilitar el informe."
            else
                "Guía cargada: ",
            color = SecGris,
            fontSize = 12.sp
        )

        Button(
            onClick = {
                crearPDF.launch("Informe_Circuitos_Secuenciales.pdf")
            },
            enabled = nombreGuia.isNotBlank() && textoGuia.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecNaranja,
                contentColor = Color.Black,
                disabledContainerColor = SecPanel,
                disabledContentColor = SecGris
            )
        ) {
            Text(
                text = "GENERAR INFORME PDF",
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "FIN DEL MÓDULO DE CIRCUITOS SECUENCIALES",
            color = SecVerde,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TarjetaContadorSecuencial(
    titulo: String,
    descripcion: String,
    valor: Int,
    bits: Int,
    modulo: Int,
    nombresBits: String,
    onPulso: () -> Unit,
    onReiniciar: () -> Unit
) {
    val binario = valor.toString(2).padStart(bits, '0')

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SecPanel)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                color = SecNaranja,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )

            Text(
                text = descripcion,
                color = SecGris,
                fontSize = 13.sp
            )

            Text(
                text = "ESTADO ACTUAL",
                color = SecTexto,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = valor.toString(),
                color = SecVerde,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Bits: $nombresBits",
                color = SecGris
            )

            Text(
                text = binario,
                color = SecVerde,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Secuencia: ${secuenciaSecuencial(modulo)}",
                color = SecTexto,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPulso,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecNaranja,
                        contentColor = Color.Black
                    )
                ) {
                    Text("PULSO DE RELOJ", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onReiniciar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("REINICIAR")
                }
            }

            Text(
                text = "Progreso: ${valor + 1} de $modulo estados",
                color = SecGris,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun TarjetaSecuencial(
    titulo: String,
    descripcion: String,
    contenido: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SecPanel)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                color = SecNaranja,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = descripcion,
                color = SecGris,
                fontSize = 13.sp
            )

            Text(
                text = contenido,
                color = SecTexto,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}

private fun secuenciaSecuencial(modulo: Int): String {
    return (0 until modulo).joinToString(" → ")
}








