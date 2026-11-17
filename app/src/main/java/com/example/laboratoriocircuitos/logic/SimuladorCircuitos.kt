package com.example.laboratoriocircuitos.logic

data class ResultadoSimulacion(
    val valoresEntrada: Map<String, Boolean>,
    val resultadoSalida: Boolean,
    val mensaje: String
)

object SimuladorCircuitos {

    fun simular(
        circuito: CircuitoGenerado,
        entradas: Map<String, Boolean>
    ): ResultadoSimulacion {

        val variablesFaltantes = circuito.variables.filter {
            !entradas.containsKey(it)
        }

        require(variablesFaltantes.isEmpty()) {
            "Faltan valores para las entradas: ${variablesFaltantes.joinToString(", ")}"
        }

        val resultado = evaluarNodo(
            circuito.raiz,
            entradas
        )

        return ResultadoSimulacion(
            valoresEntrada = circuito.variables.associateWith {
                entradas.getValue(it)
            },
            resultadoSalida = resultado,
            mensaje = if (resultado) {
                "La salida del circuito es 1 (ACTIVADA)."
            } else {
                "La salida del circuito es 0 (DESACTIVADA)."
            }
        )
    }

    private fun evaluarNodo(
        nodo: NodoCircuitoDigital,
        entradas: Map<String, Boolean>
    ): Boolean {

        return when (nodo) {

            is NodoCircuitoDigital.Variable -> {
                entradas[nodo.nombre]
                    ?: throw IllegalArgumentException(
                        "No existe un valor para la entrada ${nodo.nombre}."
                    )
            }

            is NodoCircuitoDigital.Constante -> {
                nodo.valor
            }

            is NodoCircuitoDigital.CompuertaNOT -> {
                !evaluarNodo(nodo.entrada, entradas)
            }

            is NodoCircuitoDigital.CompuertaAND -> {
                evaluarNodo(nodo.entradaA, entradas) &&
                    evaluarNodo(nodo.entradaB, entradas)
            }

            is NodoCircuitoDigital.CompuertaOR -> {
                evaluarNodo(nodo.entradaA, entradas) ||
                    evaluarNodo(nodo.entradaB, entradas)
            }
        }
    }
}
