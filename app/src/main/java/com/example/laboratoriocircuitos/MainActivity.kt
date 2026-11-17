package com.example.laboratoriocircuitos

import android.os.Bundle
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.LectorPDF
import com.example.laboratoriocircuitos.logic.AnalizadorEjercicios
import com.example.laboratoriocircuitos.ui.theme.LaboratorioCircuitosTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Fondo = Color(0xFF101827)
private val Panel = Color(0xFF1C293B)
private val Naranja = Color(0xFFFF8733)
private val Texto = Color(0xFFF5F7FA)
private val TextoSecundario = Color(0xFFB8C4D6)
private val Verde = Color(0xFF70D69A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaboratorioCircuitosTheme {
                PantallaPrincipal(
                    obtenerNombreArchivo = { uri ->
                        var nombre = "Guia seleccionada.pdf"

                        contentResolver.query(
                            uri,
                            null,
                            null,
                            null,
                            null
                        )?.use { cursor ->
                            val columna = cursor.getColumnIndex(
                                OpenableColumns.DISPLAY_NAME
                            )

                            if (columna >= 0 && cursor.moveToFirst()) {
                                nombre = cursor.getString(columna) ?: nombre
                            }
                        }

                        nombre
                    },
                    leerDocumento = { uri ->
                        LectorPDF.extraerTexto(this, uri)
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaPrincipal(
    obtenerNombreArchivo: (Uri) -> String,
    leerDocumento: (Uri) -> String
) {
    var nombrePDF by remember { mutableStateOf<String?>(null) }
    var textoExtraido by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var ejercicios by remember { mutableStateOf(emptyList<com.example.laboratoriocircuitos.logic.EjercicioDetectado>()) }
    var formulaSeleccionada by remember { mutableStateOf<String?>(null) }
    var moduloActual by remember { mutableStateOf("ACTIVIDADES") }
    var datosInformeTCC by remember {
        mutableStateOf<DatosInformeTCC?>(null)
    }
    var datosSecuencialesInforme by remember {
        mutableStateOf(DatosSecuencialesInforme())
    }

    val alcance = rememberCoroutineScope()

    val selectorPDF = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            nombrePDF = obtenerNombreArchivo(uri)
            textoExtraido = ""
            mensajeError = null
            cargando = true

            alcance.launch {
                try {
                    val resultado = withContext(Dispatchers.IO) {
                        leerDocumento(uri)
                    }

                    textoExtraido = resultado

                    if (resultado.isBlank()) {
                        mensajeError =
                            "El PDF no contiene texto seleccionable. Puede ser un documento escaneado que necesita OCR."
                    }
                } catch (error: Exception) {
                    mensajeError = error.message
                        ?: "No se pudo leer el documento PDF."
                } finally {
                    cargando = false
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Fondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "LABORATORIO",
                color = Naranja,
                fontSize = 29.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "DE CIRCUITOS",
                color = Texto,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Aprende, analiza y simula circuitos digitales",
                color = TextoSecundario,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Panel),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "MÓDULOS DEL SISTEMA",
                        color = Naranja,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "ACTIVIDADES",
                            "LABORATORIO",
                            "CIRCUITOS SECUENCIALES",
                            "TCC",
                            "KARNAUGH",
                            "ÁLGEBRA BOOLEANA",
                            "INDUSTRIAL",
                            "INFORMES"
                        ).forEach { modulo ->
                            Button(
                                onClick = { moduloActual = modulo },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (moduloActual == modulo) Naranja else Fondo,
                                    contentColor = if (moduloActual == modulo) Color.Black else Texto
                                )
                            ) {
                                Text(modulo, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.height(30.dp))

            if (moduloActual == "ACTIVIDADES") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Panel
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CARGAR GUÃA DE LABORATORIO",
                        color = Naranja,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Selecciona un documento PDF para extraer el texto de sus ejercicios de circuitos digitales.",
                        color = TextoSecundario,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            selectorPDF.launch(arrayOf("application/pdf"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Naranja,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "SELECCIONAR ARCHIVO PDF",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (nombrePDF != null) {
                        Spacer(modifier = Modifier.height(18.dp))

                        HorizontalDivider(
                            color = TextoSecundario.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "DOCUMENTO SELECCIONADO",
                            color = Verde,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = nombrePDF ?: "",
                            color = Texto,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        if (cargando) {
                            CircularProgressIndicator(
                                color = Naranja
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Leyendo documento...",
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(20.dp))

                TarjetaResultado(
                    titulo = "INFORMACIÃ“N",
                    contenido = mensajeError ?: "",
                    colorTitulo = Naranja
                )
            }

            if (textoExtraido.isNotBlank()) {
                Spacer(modifier = Modifier.height(20.dp))

                TarjetaResultado(
                    titulo = "TEXTO EXTRAÃDO DEL PDF",
                    contenido = textoExtraido,
                    colorTitulo = Verde
                )

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = {
                        ejercicios = AnalizadorEjercicios.analizar(textoExtraido)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Verde,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "ANALIZAR EJERCICIOS",
                        fontWeight = FontWeight.Bold
                    )
                }

                val resultadosConFormula = ejercicios.filter {
                    it.formulasDetectadas.isNotEmpty()
                }

                if (resultadosConFormula.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(20.dp))

                    resultadosConFormula.forEach { ejercicio ->

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "PROBLEMÁTICA",
                            color = Naranja,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = ejercicio.enunciado,
                            color = Texto,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ejercicio.formulasDetectadas.forEach { formula ->

                            Text(
                                text = "FÓRMULA BOOLEANA",
                                color = Verde,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TarjetaResultado(
                                titulo = "EXPRESIÓN LÓGICA",
                                contenido = formula,
                                colorTitulo = Verde
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    formulaSeleccionada = formula
                                moduloActual = "LABORATORIO"
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Verde,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "USAR EN GENERADOR",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                                                                TarjetaSolucionActividad(formula = formula)
                        }
                    }

                } else if (ejercicios.isNotEmpty()) {

                    Text(
                        text = "No se encontraron problemáticas con fórmulas disponibles.",
                        color = TextoSecundario,
                        fontSize = 14.sp
                    )
                }
            }

            }

            Spacer(modifier = Modifier.height(25.dp))

            Spacer(modifier = Modifier.height(20.dp))

            if (moduloActual == "LABORATORIO") {
            PantallaLaboratorioBooleano(formulaInicial = formulaSeleccionada)

            Spacer(modifier = Modifier.height(20.dp))

            PantallaSimulacionGuia()

            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "HERRAMIENTAS DEL LABORATORIO",
                color = Naranja,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            TarjetaHerramienta(
                numero = "01",
                titulo = "AnÃ¡lisis de ejercicios",
                descripcion = "IdentificaciÃ³n de variables, entradas y salidas."
            )

            TarjetaHerramienta(
                numero = "02",
                titulo = "FÃ³rmulas booleanas",
                descripcion = "ObtenciÃ³n y simplificaciÃ³n de expresiones lÃ³gicas."
            )

            TarjetaHerramienta(
                numero = "03",
                titulo = "Generador de circuitos",
                descripcion = "Dibujo automÃ¡tico de compuertas y conexiones."
            )

            TarjetaHerramienta(
                numero = "04",
                titulo = "Tablas de verdad",
                descripcion = "Combinaciones de entradas y resultados de salida."
            )

            TarjetaHerramienta(
                numero = "05",
                titulo = "Simulador y resultados",
                descripcion = "Prueba las entradas y comprueba el funcionamiento."
            )

            Spacer(modifier = Modifier.height(25.dp))

            }

            when (moduloActual) {
                "CIRCUITOS SECUENCIALES" -> {
                    PantallaCircuitosSecuenciales(
                        nombreGuia = nombrePDF ?: "",
                        textoGuia = textoExtraido,
                        onDatosActualizados = { nuevosDatos ->
                            datosSecuencialesInforme = nuevosDatos
                        }
                    )
                }

                "TCC" -> {
                    PantallaTCC { destino -> moduloActual = destino }
                }
                "KARNAUGH" -> {
                    PantallaKarnaugh(
                        onResultadosActualizados = { nuevosDatos ->
                            datosInformeTCC = nuevosDatos
                        }
                    )
                }
                "ÁLGEBRA BOOLEANA" -> {
                    PantallaAlgebraBooleana()
                }

                "INDUSTRIAL" -> {
                    PantallaIndustrial()
                }

                "INFORMES" -> {
                    PantallaInformes(
                        datosTCC = datosInformeTCC,
                        datosSecuenciales = datosSecuencialesInforme
                    )
                }
            }
            Text(
                text = "PROYECTO EDUCATIVO Â· SISTEMAS DIGITALES",
                color = TextoSecundario,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun TarjetaResultado(
    titulo: String,
    contenido: String,
    colorTitulo: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                color = colorTitulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = contenido,
                color = Texto,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
fun TarjetaHerramienta(
    numero: String,
    titulo: String,
    descripcion: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = numero,
                color = Naranja,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column {
                Text(
                    text = titulo,
                    color = Texto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    color = TextoSecundario,
                    fontSize = 12.sp
                )
            }
        }
    }
}













