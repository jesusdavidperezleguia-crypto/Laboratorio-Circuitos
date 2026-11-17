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

        for (i in 0 until 20) {

            val cambio = aplicarReglas(actual) ?: break

            if (cambio.second == actual) break

            pasos.add(
                PasoSimplificacion(
                    regla = cambio.first,
                    antes = actual,
                    despues = cambio.second,
                    explicacion = cambio.first
                )
            )

            actual = cambio.second
        }

        val circuitoNuevo = GeneradorCircuitos.generar(actual)

        var equivalente = true
        val cantidadCombinaciones =
            1 shl circuitoOriginal.variables.size

        for (numero in 0 until cantidadCombinaciones) {

            val valores = circuitoOriginal.variables
                .mapIndexed { indice, variable ->
                    val desplazamiento =
                        circuitoOriginal.variables.size - indice - 1

                    variable to (((numero shr desplazamiento) and 1) == 1)
                }
                .toMap()

            val valorOriginal = SimuladorCircuitos.simular(
                circuitoOriginal,
                valores
            ).resultadoSalida

            val valorNuevo = SimuladorCircuitos.simular(
                circuitoNuevo,
                valores
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

    private fun aplicarReglas(
        expresion: String
    ): Pair<String, String>? {

        val expresionLimpia = expresion.replace(" ", "")

        // DISTRIBUTIVA: AB + AC = A(B + C)
        val terminosOR = dividirNivelPrincipal(
            expresionLimpia,
            '+'
        )

        if (terminosOR.size >= 2) {

            val factoresPorTermino = terminosOR.map { termino ->
                dividirNivelPrincipal(
                    quitarParentesisExternos(termino),
                    '*'
                )
            }

            val factoresComunes = factoresPorTermino.first()
                .filter { factor ->
                    factoresPorTermino.drop(1).all { factores ->
                        factores.contains(factor)
                    }
                }

            if (factoresComunes.isNotEmpty()) {

                val factorComun = factoresComunes.joinToString("*")

                val restantes = factoresPorTermino.map { factores ->
                    val copia = factores.toMutableList()

                    factoresComunes.forEach { factor ->
                        copia.remove(factor)
                    }

                    when {
                        copia.isEmpty() -> "1"
                        copia.size == 1 -> copia.first()
                        else -> copia.joinToString("*")
                    }
                }

                val agrupacion = restantes.joinToString("+")

                val resultado = "$factorComun*($agrupacion)"

                return Pair(
                    "Propiedad distributiva: extracción de factor común",
                    resultado
                )
            }
        }

        // Reglas básicas de simplificación.
        val variables = Regex("[A-Z]")
            .findAll(expresionLimpia)
            .map { it.value }
            .distinct()
            .toList()

        for (variable in variables) {

            val reglas = listOf(
                Triple("$variable*1", variable, "Identidad AND"),
                Triple("$variable+0", variable, "Identidad OR"),
                Triple("$variable*0", "0", "Dominación AND"),
                Triple("$variable+1", "1", "Dominación OR"),
                Triple("$variable*$variable", variable, "Idempotencia AND"),
                Triple("$variable+$variable", variable, "Idempotencia OR"),
                Triple("$variable*!$variable", "0", "Complemento AND"),
                Triple("$variable+!$variable", "1", "Complemento OR"),
                Triple("$variable*($variable+B)", variable, "Absorción AND"),
                Triple("$variable+($variable*B)", variable, "Absorción OR")
            )

            for ((patron, reemplazo, nombre) in reglas) {

                if (expresionLimpia.contains(patron)) {
                    return Pair(
                        nombre,
                        expresionLimpia.replaceFirst(patron, reemplazo)
                    )
                }
            }
        }

        if (expresionLimpia.contains("!!")) {
            return Pair(
                "Doble negación",
                expresionLimpia.replace("!!", "")
            )
        }

        return null
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

    private fun quitarParentesisExternos(
        expresion: String
    ): String {

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
