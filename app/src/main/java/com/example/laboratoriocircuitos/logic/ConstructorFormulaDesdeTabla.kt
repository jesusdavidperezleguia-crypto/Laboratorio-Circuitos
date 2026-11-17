package com.example.laboratoriocircuitos.logic

object ConstructorFormulaDesdeTabla {

    fun generar(
        variables: List<String>,
        salidas: List<Boolean>,
        nombreSalida: String = "Z"
    ): String {

        require(variables.isNotEmpty()) {
            "La tabla debe tener al menos una variable."
        }

        require(variables.size <= 10) {
            "Máximo permitido: 10 variables."
        }

        require(variables.distinct().size == variables.size) {
            "La lista contiene variables repetidas."
        }

        require(variables.all { it.matches(Regex("[A-Z]")) }) {
            "Cada variable debe ser una sola letra mayúscula."
        }

        val cantidadFilas = 1 shl variables.size

        require(salidas.size == cantidadFilas) {
            "La tabla debe tener exactamente $cantidadFilas filas de salida."
        }

        val minterminos = salidas.mapIndexedNotNull { indice, salida ->

            if (!salida) {
                null
            } else {

                val literales = variables.mapIndexed { posicion, variable ->

                    val desplazamiento = variables.size - posicion - 1
                    val valor = ((indice shr desplazamiento) and 1) == 1

                    if (valor) variable else "!$variable"
                }

                "(${literales.joinToString("*")})"
            }
        }

        val expresion = when {
            minterminos.isEmpty() -> "0"
            minterminos.size == cantidadFilas -> "1"
            else -> minterminos.joinToString("+")
        }

        return "$nombreSalida = $expresion"
    }
}
