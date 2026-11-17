package com.example.laboratoriocircuitos

import com.example.laboratoriocircuitos.logic.ComponenteEditor
import com.example.laboratoriocircuitos.logic.ConexionEditor

data class DatosSecuencialesInforme(
    val contador6: Int = 0,
    val contador9: Int = 0,
    val contador7490: Int = 0,
    val componentes: List<ComponenteEditor> = emptyList(),
    val conexiones: List<ConexionEditor> = emptyList()
)
