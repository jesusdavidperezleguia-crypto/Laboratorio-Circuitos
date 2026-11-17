package com.example.laboratoriocircuitos.logic

object NormalizadorBooleano {

    fun normalizar(formula: String): String {

        var expresion = formula
            .substringAfter("=", formula)
            .uppercase()
            .replace(Regex("\\s+"), "")

        // Convierte letras con barra superior: Ā -> !A
        expresion = expresion.replace(
            Regex("([A-Z])\\u0304")
        ) {
            "!${it.groupValues[1]}"
        }

        // Unifica los operadores lógicos
        expresion = expresion
            .replace("¬", "!")
            .replace("·", "*")
            .replace("∧", "*")
            .replace("∨", "+")

        return expresion
    }
}
