package com.example.laboratoriocircuitos.logic

data class FilaVerdad(
    val entradas: Map<String, Boolean>,
    val salida: Boolean
)

data class TablaVerdadResultado(
    val variables: List<String>,
    val filas: List<FilaVerdad>
)

object GeneradorTablaVerdad {

    fun generar(formula: String): TablaVerdadResultado {

        val variables = GeneradorBooleano.identificarVariables(formula)

        require(variables.isNotEmpty()) {
            "La fórmula debe contener al menos una variable."
        }

        require(variables.size <= 10) {
            "La fórmula tiene demasiadas variables. Máximo permitido: 10."
        }

        val cantidadFilas = 1 shl variables.size

        val filas = (0 until cantidadFilas).map { indice ->

            val entradas = variables.mapIndexed { posicion, variable ->

                val desplazamiento = variables.size - posicion - 1

                val valor = ((indice shr desplazamiento) and 1) == 1

                variable to valor

            }.toMap()

            val salida = GeneradorBooleano.evaluar(
                formula,
                entradas
            )

            FilaVerdad(
                entradas = entradas,
                salida = salida
            )
        }

        return TablaVerdadResultado(
            variables = variables,
            filas = filas
        )
    }
}
