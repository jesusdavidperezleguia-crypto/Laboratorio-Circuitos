package com.example.laboratoriocircuitos.logic

sealed class NodoCircuitoDigital {

    data class Variable(val nombre: String) : NodoCircuitoDigital()

    data class Constante(val valor: Boolean) : NodoCircuitoDigital()

    data class CompuertaNOT(
        val entrada: NodoCircuitoDigital
    ) : NodoCircuitoDigital()

    data class CompuertaAND(
        val entradaA: NodoCircuitoDigital,
        val entradaB: NodoCircuitoDigital
    ) : NodoCircuitoDigital()

    data class CompuertaOR(
        val entradaA: NodoCircuitoDigital,
        val entradaB: NodoCircuitoDigital
    ) : NodoCircuitoDigital()
}

data class CircuitoGenerado(
    val expresion: String,
    val variables: List<String>,
    val raiz: NodoCircuitoDigital,
    val cantidadCompuertas: Int
)

object GeneradorCircuitos {

    fun generar(formula: String): CircuitoGenerado {

        val expresion = NormalizadorBooleano.normalizar(formula)

        require(expresion.isNotEmpty()) {
            "La expresión está vacía."
        }

        val parser = ParserCircuito(expresion)
        val raiz = parser.analizar()

        val variables = GeneradorBooleano
            .identificarVariables(formula)

        return CircuitoGenerado(
            expresion = expresion,
            variables = variables,
            raiz = raiz,
            cantidadCompuertas = contarCompuertas(raiz)
        )
    }

    private fun contarCompuertas(
        nodo: NodoCircuitoDigital
    ): Int {

        return when (nodo) {

            is NodoCircuitoDigital.Variable,
            is NodoCircuitoDigital.Constante -> 0

            is NodoCircuitoDigital.CompuertaNOT ->
                1 + contarCompuertas(nodo.entrada)

            is NodoCircuitoDigital.CompuertaAND ->
                1 +
                contarCompuertas(nodo.entradaA) +
                contarCompuertas(nodo.entradaB)

            is NodoCircuitoDigital.CompuertaOR ->
                1 +
                contarCompuertas(nodo.entradaA) +
                contarCompuertas(nodo.entradaB)
        }
    }

    private class ParserCircuito(
        private val texto: String
    ) {

        private var posicion = 0

        fun analizar(): NodoCircuitoDigital {

            val resultado = leerOR()

            require(posicion == texto.length) {
                "Símbolo inesperado: ${texto[posicion]}"
            }

            return resultado
        }

        private fun leerOR(): NodoCircuitoDigital {

            var resultado = leerAND()

            while (
                coincide('+') ||
                coincide('|')
            ) {
                avanzar()

                resultado = NodoCircuitoDigital.CompuertaOR(
                    resultado,
                    leerAND()
                )
            }

            return resultado
        }

        private fun leerAND(): NodoCircuitoDigital {

            var resultado = leerUnario()

            while (
                coincide('&') ||
                coincide('·') ||
                coincide('*') ||
                coincide('.')
            ) {
                avanzar()

                resultado = NodoCircuitoDigital.CompuertaAND(
                    resultado,
                    leerUnario()
                )
            }

            return resultado
        }

        private fun leerUnario(): NodoCircuitoDigital {

            if (
                coincide('¬') ||
                coincide('!') ||
                coincide('~')
            ) {
                avanzar()

                return NodoCircuitoDigital.CompuertaNOT(
                    leerUnario()
                )
            }

            var resultado = leerPrimario()

            while (coincide('\'')) {
                avanzar()

                resultado = NodoCircuitoDigital.CompuertaNOT(
                    resultado
                )
            }

            return resultado
        }

        private fun leerPrimario(): NodoCircuitoDigital {

            require(posicion < texto.length) {
                "Falta una variable o un valor lógico."
            }

            val simbolo = texto[posicion]

            if (simbolo == '(') {

                avanzar()

                val resultado = leerOR()

                require(coincide(')')) {
                    "Falta cerrar un paréntesis."
                }

                avanzar()

                return resultado
            }

            if (simbolo == '0' || simbolo == '1') {

                avanzar()

                return NodoCircuitoDigital.Constante(
                    simbolo == '1'
                )
            }

            if (simbolo.isLetter()) {

                avanzar()

                return NodoCircuitoDigital.Variable(
                    simbolo.toString()
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

