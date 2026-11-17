package com.example.laboratoriocircuitos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.SimuladorCircuitos

private val SalidaPanel = Color(0xFF1C293B)
private val SalidaNaranja = Color(0xFFFF8733)
private val SalidaVerde = Color(0xFF70D69A)
private val SalidaTexto = Color(0xFFF5F7FA)
private val SalidaSecundario = Color(0xFFB8C4D6)

@Composable
fun PanelSalidasIndustriales(
    entradas: Map<String, Boolean>
) {
    val formulaAlarma = "AL = D+!E"
    val formulaApertura = "AP = A*B*C*!D*E*F"
    val formulaRegistro = "R = A*B"

    val circuitoAlarma = remember {
        GeneradorCircuitos.generar(formulaAlarma)
    }

    val circuitoApertura = remember {
        GeneradorCircuitos.generar(formulaApertura)
    }

    val circuitoRegistro = remember {
        GeneradorCircuitos.generar(formulaRegistro)
    }

    val alarma = remember(entradas) {
        SimuladorCircuitos.simular(circuitoAlarma, entradas).resultadoSalida
    }

    val apertura = remember(entradas) {
        SimuladorCircuitos.simular(circuitoApertura, entradas).resultadoSalida
    }

    val registro = remember(entradas) {
        SimuladorCircuitos.simular(circuitoRegistro, entradas).resultadoSalida
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SalidaPanel),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "SALIDAS DEL SISTEMA INDUSTRIAL",
                color = SalidaNaranja,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                "Resultados calculados a partir de las seis entradas digitales.",
                color = SalidaSecundario,
                fontSize = 12.sp
            )

            ResultadoSalida(
                nombre = "ALARMA DE SEGURIDAD",
                formula = formulaAlarma,
                descripcion = "Se activa ante una emergencia o cuando el paso está obstruido.",
                activa = alarma
            )

            HorizontalDivider(color = SalidaSecundario.copy(alpha = 0.25f))

            ResultadoSalida(
                nombre = "APERTURA DE PUERTA",
                formula = formulaApertura,
                descripcion = "Requiere tarjeta, PIN, puerta cerrada, ausencia de emergencia, paso despejado y presencia detectada.",
                activa = apertura
            )

            HorizontalDivider(color = SalidaSecundario.copy(alpha = 0.25f))

            ResultadoSalida(
                nombre = "REGISTRO DE ACCESO",
                formula = formulaRegistro,
                descripcion = "Se activa cuando la tarjeta y el PIN han sido validados.",
                activa = registro
            )
        }
    }
}

@Composable
private fun ResultadoSalida(
    nombre: String,
    formula: String,
    descripcion: String,
    activa: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            nombre,
            color = SalidaTexto,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Text(
            formula,
            color = SalidaNaranja,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            descripcion,
            color = SalidaSecundario,
            fontSize = 11.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (activa) "SALIDA ACTIVADA" else "SALIDA DESACTIVADA",
                color = if (activa) SalidaVerde else SalidaNaranja,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Text(
                "Valor: ${if (activa) 1 else 0}",
                color = SalidaTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
