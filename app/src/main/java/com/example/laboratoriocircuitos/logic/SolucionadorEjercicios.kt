package com.example.laboratoriocircuitos.logic

data class ResultadoSolucionEjercicio(
    val formulaOriginal: String,
    val formulaNormalizada: String,
    val variables: List<String>,
    val tablaVerdad: TablaVerdadResultado,
    val circuito: CircuitoGenerado,
    val simplificacion: ResultadoSimplificacion,
    val circuitoSimplificado: CircuitoGenerado,
    val verificacionTablaCorrecta: Boolean,
    val verificacionEquivalenciaCorrecta: Boolean,
    val mensaje: String
)

object SolucionadorEjercicios {

    fun solucionar(formula: String): ResultadoSolucionEjercicio {

        require(formula.isNotBlank()) {
            "Debes introducir una fórmula booleana."
        }

        // 1. Construir el circuito original.
        val circuitoOriginal = GeneradorCircuitos.generar(formula)

        // 2. Identificar las variables.
        val variables = circuitoOriginal.variables

        require(variables.isNotEmpty()) {
            "La fórmula debe contener al menos una variable."
        }

        require(variables.size <= 8) {
            "El solucionador admite hasta 8 variables."
        }

        // 3. Generar la tabla de verdad.
        val tabla = GeneradorTablaVerdad.generar(formula)

        // 4. Simplificar la expresión y obtener sus pasos.
        val simplificacion = SimplificadorBooleano.simplificar(formula)

        // 5. Construir el circuito de la expresión simplificada.
        val circuitoSimplificado = GeneradorCircuitos.generar(
            simplificacion.simplificada
        )

        // 6. Comprobar cada fila de la tabla mediante el simulador.
        var tablaCorrecta = true
        var equivalenciaCorrecta = true

        for (fila in tabla.filas) {

            val resultadoOriginal = SimuladorCircuitos.simular(
                circuitoOriginal,
                fila.entradas
            ).resultadoSalida

            val resultadoSimplificado = SimuladorCircuitos.simular(
                circuitoSimplificado,
                fila.entradas
            ).resultadoSalida

            // La simulación original debe coincidir con la tabla.
            if (resultadoOriginal != fila.salida) {
                tablaCorrecta = false
            }

            // Ambas expresiones deben producir la misma salida.
            if (resultadoOriginal != resultadoSimplificado) {
                equivalenciaCorrecta = false
            }
        }

        val equivalenciaFinal =
            equivalenciaCorrecta && simplificacion.equivalente

        val mensaje = when {
            !tablaCorrecta ->
                "Se detectó una diferencia entre la tabla y la simulación."

            !equivalenciaFinal ->
                "No se pudo confirmar la equivalencia de las expresiones."

            else ->
                "Actividad resuelta. Tabla, circuito y simplificación verificados."
        }

        return ResultadoSolucionEjercicio(
            formulaOriginal = formula,
            formulaNormalizada = circuitoOriginal.expresion,
            variables = variables,
            tablaVerdad = tabla,
            circuito = circuitoOriginal,
            simplificacion = simplificacion,
            circuitoSimplificado = circuitoSimplificado,
            verificacionTablaCorrecta = tablaCorrecta,
            verificacionEquivalenciaCorrecta = equivalenciaFinal,
            mensaje = mensaje
        )
    }
}
