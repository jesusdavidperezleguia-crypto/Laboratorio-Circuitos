package com.example.laboratoriocircuitos.logic

object GeneradorBooleano {

    fun identificarVariables(formula: String): List<String> {
        val expresion = obtenerExpresion(formula)

        return expresion
            .filter { it.isLetter() }
            .map { it.uppercaseChar().toString() }
            .distinct()
            .sorted()
    }

    fun evaluar(
        formula: String,
        valores: Map<String, Boolean>
    ): Boolean {

        val expresion = obtenerExpresion(formula)
        val parser = Parser(expresion, valores)

        return parser.analizar()
    }

    private fun obtenerExpresion(formula: String): String {
        return NormalizadorBooleano.normalizar(formula)
    }

    private class Parser(
        private val texto: String,
        private val valores: Map<String, Boolean>
    ) {
        private var posicion = 0

        fun analizar(): Boolean {
            require(texto.isNotEmpty()) {
                "La expresión booleana está vacía."
            }

            val resultado = leerOr()

            require(posicion == texto.length) {
                "Símbolo inesperado: ${texto[posicion]}"
            }

            return resultado
        }

        // OR: A + B
        private fun leerOr(): Boolean {
            var resultado = leerAnd()

            while (coincide('+') || coincide('|') || coincide('↓')) {
                val operador = texto[posicion]
                avanzar()
                val derecho = leerAnd()

                resultado = if (operador == '↓') {
                    !(resultado || derecho)
                } else {
                    resultado || derecho
                }
            }

            return resultado
        }

        // AND: A · B
        private fun leerAnd(): Boolean {
            var resultado = leerUnario()

            while (
                coincide('&') ||
                coincide('·') ||
                coincide('*') ||
                coincide('.') ||
                coincide('↑')
            ) {
                val operador = texto[posicion]
                avanzar()
                val derecho = leerUnario()

                resultado = if (operador == '↑') {
                    !(resultado && derecho)
                } else {
                    resultado && derecho
                }
            }

            return resultado
        }

        // NOT: ¬A o !A
        private fun leerUnario(): Boolean {

            if (
                coincide('¬') ||
                coincide('!') ||
                coincide('~')
            ) {
                avanzar()
                return !leerUnario()
            }

            var resultado = leerPrimario()

            // También admite A'
            while (coincide('\'')) {
                avanzar()
                resultado = !resultado
            }

            return resultado
        }

        private fun leerPrimario(): Boolean {

            require(posicion < texto.length) {
                "Falta una variable o un valor lógico."
            }

            val simbolo = texto[posicion]

            if (simbolo == '(') {
                avanzar()
                val resultado = leerOr()

                require(coincide(')')) {
                    "Falta cerrar un paréntesis."
                }

                avanzar()
                return resultado
            }

            if (simbolo == '0' || simbolo == '1') {
                avanzar()
                return simbolo == '1'
            }

            if (simbolo.isLetter()) {
                avanzar()

                val nombre = simbolo.toString()

                return valores[nombre]
                    ?: throw IllegalArgumentException(
                        "No se asignó un valor a la variable $nombre."
                    )
            }

            throw IllegalArgumentException(
                "Símbolo no reconocido: $simbolo"
            )
        }

        private fun coincide(simbolo: Char): Boolean {
            return posicion < texto.length &&
                texto[posicion] == simbolo
        }

        private fun avanzar() {
            posicion++
        }
    }
}


