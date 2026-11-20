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

    private data class Cambio(
        val expresion: String,
        val regla: String,
        val explicacion: String
    )

    fun simplificar(formula: String): ResultadoSimplificacion {
        val circuitoOriginal = GeneradorCircuitos.generar(formula)
        val original = circuitoOriginal.expresion

        require(circuitoOriginal.variables.size <= 8) {
            "El módulo admite hasta 8 variables."
        }

        var actual = original
        val pasos = mutableListOf<PasoSimplificacion>()

        var iteraciones = 0

        while (iteraciones < 100) {
            val cambio = simplificarUnPaso(actual) ?: break

            if (cambio.expresion == actual) break

            pasos.add(
                PasoSimplificacion(
                    regla = cambio.regla,
                    antes = actual,
                    despues = cambio.expresion,
                    explicacion = cambio.explicacion
                )
            )

            actual = cambio.expresion
            iteraciones++
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

    private fun simplificarUnPaso(expresion: String): Cambio? {
        val e = expresion.replace(" ", "")

        if (e.isEmpty()) return null

        val sinParentesis = quitarParentesisExternos(e)

        if (sinParentesis != e) {
            return Cambio(
                sinParentesis,
                "Eliminación de paréntesis",
                "Se eliminaron paréntesis externos que no modifican la prioridad lógica."
            )
        }

        if (e.startsWith("!!")) {
            return Cambio(
                e.removePrefix("!!"),
                "Doble negación",
                "La negación de una negación equivale a la variable original."
            )
        }

        val suma = dividirNivelPrincipal(e, '+')

        if (suma.size > 1) {

            // Primero se simplifican los términos internos.
            for (i in suma.indices) {
                val cambioInterno = simplificarUnPaso(suma[i])

                if (cambioInterno != null) {
                    val nuevos = suma.toMutableList()
                    nuevos[i] = cambioInterno.expresion

                    return Cambio(
                        nuevos.joinToString("+"),
                        cambioInterno.regla,
                        cambioInterno.explicacion
                    )
                }
            }

            val cambioDirecto = simplificarSuma(suma)

            if (cambioDirecto != null) return cambioDirecto

            return factorizarSuma(suma)
        }

        val producto = dividirNivelPrincipal(e, '*')

        if (producto.size > 1) {

            // Primero se simplifican los factores internos.
            for (i in producto.indices) {
                val factorOriginal = producto[i]
                val factorInterior = quitarParentesisExternos(factorOriginal)
                val cambioInterno = simplificarUnPaso(factorInterior)

                if (cambioInterno != null) {
                    val nuevos = producto.toMutableList()

                    val resultadoInterno = cambioInterno.expresion
                    val necesitaParentesis =
                        dividirNivelPrincipal(resultadoInterno, '+').size > 1

                    nuevos[i] = if (necesitaParentesis) {
                        "($resultadoInterno)"
                    } else {
                        resultadoInterno
                    }

                    return Cambio(
                        nuevos.joinToString("*"),
                        cambioInterno.regla,
                        cambioInterno.explicacion
                    )
                }
            }

            return simplificarProducto(producto)
        }

        return null
    }

    private fun simplificarSuma(entrada: List<String>): Cambio? {
        val terminos = entrada
            .map { quitarParentesisExternos(it) }
            .filter { it.isNotBlank() }

        if ("1" in terminos) {
            return Cambio(
                "1",
                "Ley de dominación OR",
                "Una entrada verdadera hace que toda la operación OR sea verdadera."
            )
        }

        val sinCeros = terminos.filterNot { it == "0" }

        if (sinCeros.isEmpty()) {
            return Cambio(
                "0",
                "Elemento neutro OR",
                "La suma lógica de ceros es cero."
            )
        }

        if (sinCeros.distinct().size < sinCeros.size) {
            return Cambio(
                sinCeros.distinct().joinToString("+"),
                "Idempotencia OR",
                "Una variable sumada consigo misma conserva su valor."
            )
        }

        if (contieneComplementos(sinCeros)) {
            return Cambio(
                "1",
                "Ley del complemento OR",
                "Una variable OR con su complemento siempre produce uno."
            )
        }

        val eliminados = sinCeros.filterIndexed { i, termino ->
            val factores = dividirNivelPrincipal(termino, '*').toSet()

            sinCeros.indices.any { j ->
                if (i == j) {
                    false
                } else {
                    val otros = dividirNivelPrincipal(
                        sinCeros[j],
                        '*'
                    ).toSet()

                    otros.size < factores.size &&
                        otros.containsAll(factores)
                }
            }
        }

        if (eliminados.isNotEmpty()) {
            val resultado = sinCeros.filterNot {
                it in eliminados
            }.distinct()

            return Cambio(
                resultado.joinToString("+"),
                "Absorción OR",
                "Se eliminaron términos que ya están contenidos en otro término más simple."
            )
        }

        if (sinCeros.size != terminos.size) {
            return Cambio(
                sinCeros.joinToString("+"),
                "Elemento neutro OR",
                "Se eliminaron los ceros de una operación OR."
            )
        }

        return null
    }

    private fun simplificarProducto(entrada: List<String>): Cambio? {
        val factores = entrada
            .map { quitarParentesisExternos(it) }
            .filter { it.isNotBlank() }

        if ("0" in factores) {
            return Cambio(
                "0",
                "Ley de dominación AND",
                "Una entrada falsa hace que todo el producto lógico sea falso."
            )
        }

        val sinUnos = factores.filterNot { it == "1" }

        if (sinUnos.isEmpty()) {
            return Cambio(
                "1",
                "Elemento neutro AND",
                "El producto lógico de unos es uno."
            )
        }

        if (sinUnos.distinct().size < sinUnos.size) {
            return Cambio(
                sinUnos.distinct().joinToString("*"),
                "Idempotencia AND",
                "Una variable multiplicada por sí misma conserva su valor."
            )
        }

        if (contieneComplementos(sinUnos)) {
            return Cambio(
                "0",
                "Ley del complemento AND",
                "Una variable AND con su complemento siempre produce cero."
            )
        }

        val eliminados = sinUnos.filterIndexed { i, factor ->
            val terminos = dividirNivelPrincipal(factor, '+').toSet()

            sinUnos.indices.any { j ->
                if (i == j) {
                    false
                } else {
                    val otros = dividirNivelPrincipal(
                        sinUnos[j],
                        '+'
                    ).toSet()

                    otros.size < terminos.size &&
                        otros.containsAll(terminos)
                }
            }
        }

        if (eliminados.isNotEmpty()) {
            val resultado = sinUnos.filterNot {
                it in eliminados
            }.distinct()

            return Cambio(
                resultado.joinToString("*"),
                "Absorción AND",
                "Se eliminaron factores que ya están contenidos en otro factor más simple."
            )
        }

        if (sinUnos.size != factores.size) {
            return Cambio(
                sinUnos.joinToString("*"),
                "Elemento neutro AND",
                "Se eliminaron los unos de una operación AND."
            )
        }

        return null
    }

    private fun factorizarSuma(terminos: List<String>): Cambio? {
        if (terminos.size < 2) return null

        val factoresPorTermino = terminos.map { termino ->
            dividirNivelPrincipal(
                quitarParentesisExternos(termino),
                '*'
            )
        }

        val comunes = factoresPorTermino.first().filter { factor ->
            factoresPorTermino.all { lista ->
                factor in lista
            }
        }.distinct()

        if (comunes.isEmpty()) return null

        val restantes = factoresPorTermino.map { lista ->
            val copia = lista.toMutableList()

            comunes.forEach { comun ->
                copia.remove(comun)
            }

            copia.joinToString("*")
        }

        if (restantes.any { it.isBlank() }) return null

        val parteComun = comunes.joinToString("*")
        val parteRestante = restantes.joinToString("+")

        val resultado = "$parteComun*($parteRestante)"

        return Cambio(
            resultado,
            "Factor común distributivo",
            "Se extrajo el factor común de los términos de una suma aplicando la propiedad distributiva."
        )
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
                        partes.add(
                            expresion.substring(inicio, i)
                        )
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
                resultado = resultado.substring(
                    1,
                    resultado.length - 1
                )
            } else {
                break
            }
        }

        return resultado
    }
}