package com.example.laboratoriocircuitos.logic

data class GrupoKarnaugh(
    val patron: String,
    val expresion: String,
    val minterminos: List<Int>,
    val celdas: List<Pair<Int, Int>>
)

data class ResultadoKarnaugh(
    val variables: List<String>,
    val filasGray: List<String>,
    val columnasGray: List<String>,
    val mapa: List<List<Int>>,
    val minterminos: List<Int>,
    val grupos: List<GrupoKarnaugh>,
    val formulaSimplificada: String
)

object GeneradorKarnaugh {

    private data class Implicante(
        val patron: String,
        val minterminos: Set<Int>
    )

    fun generar(formula: String): ResultadoKarnaugh {

        val tabla = GeneradorTablaVerdad.generar(formula)
        val variables = tabla.variables

        require(variables.size in 2..6) {
            "El mapa de Karnaugh admite de 2 a 6 variables."
        }

        val minterminos = tabla.filas.mapIndexedNotNull { indice, fila ->
            if (fila.salida) indice else null
        }

        val bitsFila = variables.size / 2
        val bitsColumna = variables.size - bitsFila

        val filasGray = codigoGray(bitsFila)
        val columnasGray = codigoGray(bitsColumna)

        val mapa = filasGray.map { fila ->
            columnasGray.map { columna ->
                val binario = fila + columna
                binario.toInt(2)
            }
        }

        if (minterminos.isEmpty()) {
            return ResultadoKarnaugh(
                variables,
                filasGray,
                columnasGray,
                mapa,
                minterminos,
                emptyList(),
                "0"
            )
        }

        if (minterminos.size == (1 shl variables.size)) {
            val implicante = GrupoKarnaugh(
                patron = "-".repeat(variables.size),
                expresion = "1",
                minterminos = minterminos,
                celdas = mapa.indices.flatMap { fila ->
                    mapa[fila].indices.map { columna ->
                        Pair(fila, columna)
                    }
                }
            )

            return ResultadoKarnaugh(
                variables,
                filasGray,
                columnasGray,
                mapa,
                minterminos,
                listOf(implicante),
                "1"
            )
        }

        val implicantes = obtenerImplicantesPrimos(
            minterminos,
            variables.size
        )

        val seleccionados = seleccionarMejorCobertura(
            implicantes,
            minterminos,
            variables
        )

        val grupos = seleccionados.map { implicante ->

            val celdas = mapa.indices.flatMap { fila ->
                mapa[fila].indices.mapNotNull { columna ->
                    if (mapa[fila][columna] in implicante.minterminos) {
                        Pair(fila, columna)
                    } else {
                        null
                    }
                }
            }

            GrupoKarnaugh(
                patron = implicante.patron,
                expresion = convertirExpresion(
                    implicante.patron,
                    variables
                ),
                minterminos = implicante.minterminos.sorted(),
                celdas = celdas
            )
        }

        val simplificada = grupos.joinToString(" + ") {
            it.expresion
        }

        return ResultadoKarnaugh(
            variables = variables,
            filasGray = filasGray,
            columnasGray = columnasGray,
            mapa = mapa,
            minterminos = minterminos,
            grupos = grupos,
            formulaSimplificada = simplificada
        )
    }

    private fun codigoGray(bits: Int): List<String> {
        return when (bits) {
            1 -> listOf("0", "1")
            2 -> listOf("00", "01", "11", "10")
            3 -> listOf("000", "001", "011", "010", "110", "111", "101", "100")
            else -> throw IllegalArgumentException(
                "Cantidad de bits no admitida."
            )
        }
    }

    private fun obtenerImplicantesPrimos(
        minterminos: List<Int>,
        cantidadVariables: Int
    ): List<Implicante> {

        var actuales = minterminos.map { numero ->
            Implicante(
                patron = numero.toString(2)
                    .padStart(cantidadVariables, '0'),
                minterminos = setOf(numero)
            )
        }

        val primos = mutableMapOf<String, Implicante>()

        while (actuales.isNotEmpty()) {

            val usados = mutableSetOf<String>()
            val siguientes = mutableMapOf<String, Implicante>()

            for (i in actuales.indices) {
                for (j in i + 1 until actuales.size) {

                    val primero = actuales[i]
                    val segundo = actuales[j]

                    val combinado = combinar(
                        primero.patron,
                        segundo.patron
                    ) ?: continue

                    usados.add(primero.patron)
                    usados.add(segundo.patron)

                    val cubiertos =
                        primero.minterminos + segundo.minterminos

                    val existente = siguientes[combinado]

                    siguientes[combinado] = Implicante(
                        combinado,
                        (existente?.minterminos ?: emptySet()) + cubiertos
                    )
                }
            }

            actuales.filter {
                it.patron !in usados
            }.forEach {
                primos[it.patron] = it
            }

            actuales = siguientes.values.toList()
        }

        return primos.values.toList()
    }

    private fun combinar(
        primero: String,
        segundo: String
    ): String? {

        var diferencias = 0
        val resultado = StringBuilder()

        for (i in primero.indices) {

            val a = primero[i]
            val b = segundo[i]

            if (a == b) {
                resultado.append(a)
            } else {
                if (a == '-' || b == '-') {
                    return null
                }

                diferencias++
                resultado.append('-')
            }
        }

        return if (diferencias == 1) {
            resultado.toString()
        } else {
            null
        }
    }

    private fun seleccionarMejorCobertura(
        implicantes: List<Implicante>,
        minterminos: List<Int>,
        variables: List<String>
    ): List<Implicante> {

        var mejor: List<Implicante>? = null

        fun cantidadLiterales(implicantesElegidos: List<Implicante>): Int {
            return implicantesElegidos.sumOf {
                it.patron.count { simbolo -> simbolo != '-' }
            }
        }

        fun buscar(
            cubiertos: Set<Int>,
            elegidos: List<Implicante>
        ) {

            if (cubiertos.containsAll(minterminos)) {

                val anterior = mejor

                if (
                    anterior == null ||
                    elegidos.size < anterior.size ||
                    (
                        elegidos.size == anterior.size &&
                        cantidadLiterales(elegidos) <
                        cantidadLiterales(anterior)
                    )
                ) {
                    mejor = elegidos
                }

                return
            }

            val mejorActual = mejor

            if (
                mejorActual != null &&
                elegidos.size >= mejorActual.size
            ) {
                return
            }

            val patronesElegidos = elegidos.map {
                it.patron
            }.toSet()

            val faltante = minterminos
                .filter { it !in cubiertos }
                .minByOrNull { numero ->
                    implicantes.count {
                        numero in it.minterminos &&
                        it.patron !in patronesElegidos
                    }
                } ?: return

            val opciones = implicantes.filter {
                faltante in it.minterminos &&
                it.patron !in patronesElegidos
            }

            for (opcion in opciones) {
                buscar(
                    cubiertos + opcion.minterminos,
                    elegidos + opcion
                )
            }
        }

        buscar(emptySet(), emptyList())

        return mejor ?: throw IllegalStateException(
            "No fue posible simplificar la función."
        )
    }

    private fun convertirExpresion(
        patron: String,
        variables: List<String>
    ): String {

        val literales = patron.mapIndexedNotNull { indice, bit ->

            when (bit) {
                '1' -> variables[indice]
                '0' -> variables[indice] + "'"
                else -> null
            }
        }

        return if (literales.isEmpty()) {
            "1"
        } else {
            literales.joinToString("·")
        }
    }
}
