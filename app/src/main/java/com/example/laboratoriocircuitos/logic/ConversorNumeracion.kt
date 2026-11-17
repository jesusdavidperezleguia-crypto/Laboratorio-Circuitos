package com.example.laboratoriocircuitos.logic

data class ResultadoNumeracion(
    val decimal: Int,
    val binario: String,
    val bcd: String
)

object ConversorNumeracion {

    fun convertir(numero: Int): ResultadoNumeracion {
        require(numero in 0..9999) {
            "El número debe estar entre 0 y 9999."
        }

        val binario = numero.toString(2)

        val bcd = numero.toString()
            .map { digito ->
                (digito.digitToInt()).toString(2).padStart(4, '0')
            }
            .joinToString(" ")

        return ResultadoNumeracion(
            decimal = numero,
            binario = binario,
            bcd = bcd
        )
    }
}
