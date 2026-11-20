package com.example.laboratoriocircuitos.logic

data class PasoSimplificacion(
    val regla: String,
    val antes: String,
    val despues: String,
    val explicacion: String
)

data class ResultadoSimplificacion(
    val original: String,
    val simplificada: String,
    val pasos: List<PasoSimplificacion>,
    val equivalente: Boolean,
    val variables: List<String>
)

object SimplificadorBooleano {

    fun simplificar(formula: String): ResultadoSimplificacion {
        val circuitoOriginal = GeneradorCircuitos.generar(formula)
        val original = circuitoOriginal.expresion

        require(circuitoOriginal.variables.size <= 8) {
            "El módulo admite hasta 8 variables."
        }

        var actual = original
        val pasos = mutableListOf<PasoSimplificacion>()

        if (!original.contains('↑') && !original.contains('↓')) {
            repeat(30) {
            val siguiente = simplificarNodo(actual)

            if (siguiente == actual) return@repeat

            pasos.add(
                PasoSimplificacion(
                    regla = identificarRegla(actual, siguiente),
                    antes = actual,
                    despues = siguiente,
                    explicacion = "Se aplicaron propiedades del álgebra de Boole."
                )
            )

            actual = siguiente
        }

        }

        val circuitoNuevo = GeneradorCircuitos.generar(actual)

        var equivalente = true
        val cantidadCombinaciones = 1 shl circuitoOriginal.variables.size

        for (numero in 0 until cantidadCombinaciones) {
            val valores = circuitoOriginal.variables
                .mapIndexed { indice, variable ->
                    val desplazamiento =
                        circuitoOriginal.variables.size - indice - 1
                    variable to (((numero shr desplazamiento) and 1) == 1)
                }
                .toMap()

            val valorOriginal = SimuladorCircuitos.simular(
                circuitoOriginal, valores
            ).resultadoSalida

            val valorNuevo = SimuladorCircuitos.simular(
                circuitoNuevo, valores
            ).resultadoSalida

            if (valorOriginal != valorNuevo) {
                equivalente = false
                break
            }
        }

        return ResultadoSimplificacion(
            original = original,
            simplificada = actual,
            pasos = pasos,
            equivalente = equivalente,
            variables = circuitoOriginal.variables
        )
    }

    private fun simplificarNodo(expresion: String): String {
        var e = quitarParentesisExternos(expresion.replace(" ", ""))

        while (e.startsWith("!!")) {
            e = e.removePrefix("!!")
        }

        val suma = dividirNivelPrincipal(e, '+')

        if (suma.size > 1) {
            val terminos = suma.map { simplificarNodo(it) }
            return simplificarSuma(terminos)
        }

        val producto = dividirNivelPrincipal(e, '*')

        if (producto.size > 1) {
            val factores = producto.map { simplificarNodo(it) }
            return simplificarProducto(factores)
        }

        return e
    }

    private fun simplificarSuma(entrada: List<String>): String {
        var terminos = entrada
            .flatMap { dividirNivelPrincipal(it, '+') }
            .map { quitarParentesisExternos(it) }
            .filter { it.isNotBlank() }
            .distinct()

        if ("1" in terminos) return "1"
        terminos = terminos.filterNot { it == "0" }

        if (terminos.isEmpty()) return "0"

        // Complemento: A + !A = 1
        if (contieneComplementos(terminos)) return "1"

        // Absorción general: A + A*B = A
        val eliminados = terminos.filterIndexed { i, termino ->
            val factores = dividirNivelPrincipal(termino, '*').toSet()

            terminos.indices.any { j ->
                if (i == j) {
                    false
                } else {
                    val otro = dividirNivelPrincipal(terminos[j], '*').toSet()
                    otro.size < factores.size && otro.containsAll(factores)
                }
            }
        }

        terminos = terminos.filterNot { it in eliminados }.distinct()

        return when (terminos.size) {
            0 -> "0"
            1 -> terminos.first()
            else -> terminos.joinToString("+") { termino ->
                if (dividirNivelPrincipal(termino, '+').size > 1) {
                    "($termino)"
                } else {
                    termino
                }
            }
        }
    }

    private fun simplificarProducto(entrada: List<String>): String {
        var factores = entrada
            .flatMap { dividirNivelPrincipal(it, '*') }
            .map { quitarParentesisExternos(it) }
            .filter { it.isNotBlank() }
            .distinct()

        if ("0" in factores) return "0"
        factores = factores.filterNot { it == "1" }

        if (factores.isEmpty()) return "1"

        // Complemento: A * !A = 0
        if (contieneComplementos(factores)) return "0"

        // Absorción dual: A * (A+B) = A
        val eliminados = factores.filterIndexed { i, factor ->
            val literales = dividirNivelPrincipal(factor, '+').toSet()

            factores.indices.any { j ->
                if (i == j) {
                    false
                } else {
                    val otro = dividirNivelPrincipal(factores[j], '+').toSet()
                    otro.size < literales.size && otro.containsAll(literales)
                }
            }
        }

        factores = factores.filterNot { it in eliminados }.distinct()

        return when (factores.size) {
            0 -> "1"
            1 -> factores.first()
            else -> factores.joinToString("*") { factor ->
                if (dividirNivelPrincipal(factor, '+').size > 1) {
                    "($factor)"
                } else {
                    factor
                }
            }
        }
    }

    private fun contieneComplementos(elementos: List<String>): Boolean {
        val conjunto = elementos.toSet()

        return conjunto.any { elemento ->
            if (elemento.startsWith("!")) {
                elemento.removePrefix("!") in conjunto
            } else {
                "!$elemento" in conjunto
            }
        }
    }

    private fun identificarRegla(antes: String, despues: String): String {
        return when {
            antes.contains("+") && despues.length < antes.length ->
                "Absorción, idempotencia o complemento OR"

            antes.contains("*") && despues.length < antes.length ->
                "Absorción, idempotencia o complemento AND"

            else -> "Simplificación algebraica"
        }
    }

    private fun dividirNivelPrincipal(
        expresion: String,
        operador: Char
    ): List<String> {
        val partes = mutableListOf<String>()
        var profundidad = 0
        var inicio = 0

        for (i in expresion.indices) {
            when (expresion[i]) {
                '(' -> profundidad++
                ')' -> profundidad--
                operador -> {
                    if (profundidad == 0) {
                        partes.add(expresion.substring(inicio, i))
                        inicio = i + 1
                    }
                }
            }
        }

        partes.add(expresion.substring(inicio))
        return partes.filter { it.isNotBlank() }
    }

    private fun quitarParentesisExternos(expresion: String): String {
        var resultado = expresion

        while (
            resultado.startsWith("(") &&
            resultado.endsWith(")")
        ) {
            var profundidad = 0
            var envuelveTodo = true

            for (i in resultado.indices) {
                when (resultado[i]) {
                    '(' -> profundidad++
                    ')' -> profundidad--
                }

                if (profundidad == 0 && i != resultado.lastIndex) {
                    envuelveTodo = false
                    break
                }
            }

            if (envuelveTodo) {
                resultado = resultado.substring(1, resultado.length - 1)
            } else {
                break
            }
        }

        return resultado
    }
}
