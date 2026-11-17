package com.example.laboratoriocircuitos.logic

data class ResultadoComparacion(
    val compuertasOriginales: Int,
    val compuertasSimplificadas: Int,
    val compuertasReducidas: Int,
    val porcentajeReduccion: Double,
    val equivalentes: Boolean,
    val mensaje: String
)

object ComparadorCircuitos {

    fun comparar(
        formulaOriginal: String,
        formulaSimplificada: String
    ): ResultadoComparacion {

        val original = GeneradorCircuitos.generar(formulaOriginal)
        val simplificado = GeneradorCircuitos.generar(formulaSimplificada)

        val tablaOriginal = GeneradorTablaVerdad.generar(formulaOriginal)
        val tablaSimplificada = GeneradorTablaVerdad.generar(formulaSimplificada)

        require(tablaOriginal.variables == tablaSimplificada.variables) {
            "Las funciones no tienen las mismas variables."
        }

        val equivalentes =
            tablaOriginal.filas.size == tablaSimplificada.filas.size &&
            tablaOriginal.filas.zip(tablaSimplificada.filas).all {
                (filaOriginal, filaSimplificada) ->
                filaOriginal.salida == filaSimplificada.salida
            }

        val reduccion =
            original.cantidadCompuertas - simplificado.cantidadCompuertas

        val porcentaje = if (original.cantidadCompuertas > 0) {
            reduccion.toDouble() /
                original.cantidadCompuertas.toDouble() * 100.0
        } else {
            0.0
        }

        val mensaje = when {
            !equivalentes ->
                "ERROR: las funciones no son equivalentes."

            reduccion > 0 ->
                "Optimización lograda: se redujeron $reduccion compuertas."

            reduccion == 0 ->
                "Las funciones son equivalentes y mantienen la misma cantidad de compuertas."

            else ->
                "Las funciones son equivalentes, pero la expresión simplificada utiliza más compuertas."
        }

        return ResultadoComparacion(
            compuertasOriginales = original.cantidadCompuertas,
            compuertasSimplificadas = simplificado.cantidadCompuertas,
            compuertasReducidas = reduccion,
            porcentajeReduccion = porcentaje,
            equivalentes = equivalentes,
            mensaje = mensaje
        )
    }
}