package com.example.laboratoriocircuitos

import com.example.laboratoriocircuitos.logic.ResultadoKarnaugh
import com.example.laboratoriocircuitos.logic.TablaVerdadResultado
import com.example.laboratoriocircuitos.logic.ResultadoComparacion

data class DatosInformeTCC(
    val formulaOriginal: String,
    val resultadoKarnaugh: ResultadoKarnaugh,
    val tablaVerdad: TablaVerdadResultado,
    val comparacion: ResultadoComparacion,
    val entradasSimulacion: Map<String, Boolean>,
    val salidaOriginal: Boolean,
    val salidaSimplificada: Boolean
)
