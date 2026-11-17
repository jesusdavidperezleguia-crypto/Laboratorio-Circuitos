package com.example.laboratoriocircuitos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SensorPanel = Color(0xFF1C293B)
private val SensorNaranja = Color(0xFFFF8733)
private val SensorVerde = Color(0xFF70D69A)
private val SensorTexto = Color(0xFFF5F7FA)
private val SensorSecundario = Color(0xFFB8C4D6)

@Composable
fun PanelSensoresIndustriales(
    emergenciaActivada: Boolean,
    pasoOpticoDespejado: Boolean,
    cambiarPasoOptico: (Boolean) -> Unit,
    proximidadDetectada: Boolean,
    cambiarProximidad: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SensorPanel),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "CATÁLOGO DE SENSORES INDUSTRIALES",
                color = SensorNaranja,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                "Entradas del sistema de automatización SmartIndustries.",
                color = SensorSecundario,
                fontSize = 12.sp
            )

            HorizontalDivider(color = SensorSecundario.copy(alpha = 0.25f))

            SensorDigital(
                codigo = "D",
                nombre = "Parada de emergencia",
                funcion = "Señal de emergencia que bloquea la autorización.",
                valor = emergenciaActivada,
                esInterruptor = false
            )

            HorizontalDivider(color = SensorSecundario.copy(alpha = 0.25f))

            SensorDigital(
                codigo = "E",
                nombre = "Sensor óptico",
                funcion = "Indica si el paso se encuentra despejado.",
                valor = pasoOpticoDespejado,
                esInterruptor = true,
                cambiar = cambiarPasoOptico,
                textoActivo = "PASO DESPEJADO",
                textoInactivo = "PASO OBSTRUIDO"
            )

            HorizontalDivider(color = SensorSecundario.copy(alpha = 0.25f))

            SensorDigital(
                codigo = "F",
                nombre = "Sensor de proximidad",
                funcion = "Detecta la presencia de una persona cerca del acceso.",
                valor = proximidadDetectada,
                esInterruptor = true,
                cambiar = cambiarProximidad,
                textoActivo = "PERSONA DETECTADA",
                textoInactivo = "SIN DETECCIÓN"
            )

            HorizontalDivider(color = SensorSecundario.copy(alpha = 0.25f))

            Text(
                "RESUMEN DE ENTRADAS",
                color = SensorNaranja,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            ResumenSensor("D - Emergencia", emergenciaActivada)
            ResumenSensor("E - Óptico despejado", pasoOpticoDespejado)
            ResumenSensor("F - Proximidad", proximidadDetectada)
        }
    }
}

@Composable
private fun SensorDigital(
    codigo: String,
    nombre: String,
    funcion: String,
    valor: Boolean,
    esInterruptor: Boolean,
    cambiar: (Boolean) -> Unit = {},
    textoActivo: String = "ACTIVADO",
    textoInactivo: String = "DESACTIVADO"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "$codigo - $nombre",
                color = SensorTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Text(
                funcion,
                color = SensorSecundario,
                fontSize = 11.sp
            )

            Text(
                if (valor) textoActivo else textoInactivo,
                color = if (valor) SensorVerde else SensorNaranja,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )

            Text(
                "Estado digital: ${if (valor) 1 else 0}",
                color = SensorSecundario,
                fontSize = 11.sp
            )
        }

        if (esInterruptor) {
            Switch(
                checked = valor,
                onCheckedChange = cambiar,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SensorVerde,
                    checkedTrackColor = SensorVerde.copy(alpha = 0.35f),
                    uncheckedThumbColor = SensorTexto,
                    uncheckedTrackColor = SensorSecundario.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun ResumenSensor(
    nombre: String,
    valor: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(nombre, color = SensorTexto, fontSize = 12.sp)

        Text(
            if (valor) "1" else "0",
            color = if (valor) SensorVerde else SensorNaranja,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
