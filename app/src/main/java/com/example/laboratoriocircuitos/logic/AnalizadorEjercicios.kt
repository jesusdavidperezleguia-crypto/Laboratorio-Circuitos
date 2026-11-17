package com.example.laboratoriocircuitos.logic

data class EjercicioDetectado(
    val titulo: String,
    val enunciado: String,
    val variables: List<String>,
    val posiblesEntradas: List<String>,
    val posiblesSalidas: List<String>,
    val formulaDetectada: String = "",
    val formulasDetectadas: List<String> = emptyList(),
    val esFormulaInferida: Boolean = false
)

object AnalizadorEjercicios {

    private val encabezadoTexto = Regex(
        """(?i)^\s*(ejercicio|problema|actividad|punto)\b.*$"""
    )

    private val encabezadoNumerado = Regex(
        """^\s*\d+(?:\.\d+)*[.)]?\s+\S.*$"""
    )

    private val encabezadoLetra = Regex(
        """(?i)^\s*[a-h][.)]\s+\S.*$"""
    )

    private val patronVariable = Regex("""\b[A-Z]\b""")

    private val patronAsignacion = Regex(
        """(?i)\b([A-Z])\s*=\s*([A-Z0-9 \t()+*\.&|!~'\u00B7\u00AC\u2019\u0304\-]+)"""
    )

    private val palabrasEntrada = listOf(
        "entrada", "entradas", "sensor", "sensores",
        "interruptor", "interruptores", "pulsador",
        "pulsadores", "boton", "botones"
    )

    private val palabrasSalida = listOf(
        "salida", "salidas", "alarma", "zumbador",
        "motor", "luz", "lampara", "led"
    )

    fun analizar(texto: String): List<EjercicioDetectado> {

        if (texto.isBlank()) return emptyList()

        val bloques = mutableListOf<Pair<String, StringBuilder>>()

        var bloqueActual: StringBuilder? = null
        var tituloActual = ""

        fun guardarBloque() {
            bloqueActual?.let {
                if (it.isNotBlank()) {
                    bloques.add(tituloActual to it)
                }
            }
        }

        texto.lines().forEach { linea ->

            val lineaLimpia = linea.trim()

            if (lineaLimpia.isBlank()) {
                bloqueActual?.append("\n")
                return@forEach
            }

            val esEncabezado =
                encabezadoTexto.matches(lineaLimpia) ||
                encabezadoNumerado.matches(lineaLimpia) ||
                encabezadoLetra.matches(lineaLimpia)

            if (esEncabezado) {

                guardarBloque()

                tituloActual = lineaLimpia.take(100)
                bloqueActual = StringBuilder()

                val contenido = lineaLimpia
                    .replace(
                        Regex("""^\s*(?:ejercicio|problema|actividad|punto)\s*[\d.]*\s*[:.)-]?\s*""", RegexOption.IGNORE_CASE),
                        ""
                    )
                    .replace(
                        Regex("""^\s*\d+(?:\.\d+)*[.)]?\s*"""),
                        ""
                    )
                    .replace(
                        Regex("""^\s*[a-h][.)]\s*""", RegexOption.IGNORE_CASE),
                        ""
                    )
                    .trim()

                if (contenido.isNotBlank()) {
                    bloqueActual?.append(contenido)?.append("\n")
                }

            } else {

                if (bloqueActual == null) {
                    tituloActual = "Contenido inicial"
                    bloqueActual = StringBuilder()
                }

                bloqueActual?.append(lineaLimpia)?.append("\n")
            }
        }

        guardarBloque()

        return bloques.mapIndexed { indice, bloque ->

            val enunciado = bloque.second.toString().trim()
            val textoMinusculas = enunciado.lowercase()

            val formulasEscritas = detectarFormulas(enunciado)

            val formulaInferida = if (formulasEscritas.isEmpty()) {
                inferirFormulaAutomovil(enunciado)
            } else {
                null
            }

            val formulas = formulasEscritas.ifEmpty {
                listOfNotNull(formulaInferida)
            }

            val variables = (
                patronVariable.findAll(enunciado)
                    .map { it.value }
                    .toList() +
                formulas.flatMap {
                    GeneradorBooleano.identificarVariables(it)
                }
            )
                .distinct()
                .sorted()

            val entradas = palabrasEntrada.filter {
                textoMinusculas.contains(it)
            }

            val salidas = palabrasSalida.filter {
                textoMinusculas.contains(it)
            }

            EjercicioDetectado(
                titulo = bloque.first.ifBlank {
                    "Actividad ${indice + 1}"
                },
                enunciado = enunciado,
                variables = variables,
                posiblesEntradas = entradas,
                posiblesSalidas = salidas,
                formulaDetectada = formulas.firstOrNull() ?: "",
                formulasDetectadas = formulas,
                esFormulaInferida = formulaInferida != null
            )
        }.filter {
            it.enunciado.isNotBlank()
        }
    }

    private fun inferirFormulaAutomovil(texto: String): String? {

        val contenido = texto.lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")

        val esProblemaAutomovil =
            contenido.contains("automovil") &&
            contenido.contains("alarma") &&
            contenido.contains("cinturon") &&
            contenido.contains("llaves") &&
            contenido.contains("interruptor") &&
            contenido.contains("zumbador")

        val tieneVariables =
            Regex("""\bW\b""").containsMatchIn(texto) &&
            Regex("""\bC\b""").containsMatchIn(texto) &&
            Regex("""\bI\b""").containsMatchIn(texto) &&
            Regex("""\bZ\b""").containsMatchIn(texto)

        return if (esProblemaAutomovil && tieneVariables) {
            "Z = W*C' + W'*I"
        } else {
            null
        }
    }

    private fun detectarFormulas(texto: String): List<String> {

        return patronAsignacion.findAll(texto)
            .mapNotNull { coincidencia ->

                val formula = coincidencia.value
                    .replace(Regex("\\s+"), "")
                    .uppercase()
                    .replace(Regex("([A-Z])\\u0304")) {
                        "\u00AC${it.groupValues[1]}"
                    }

                val expresion = formula.substringAfter("=")

                val tieneOperador = expresion.any {
                    it in "+*&|!~·¬"
                }

                val cantidadVariables = Regex("[A-Z]")
                    .findAll(expresion)
                    .count()

                if (tieneOperador && cantidadVariables >= 1) {
                    formula
                } else {
                    null
                }
            }
            .distinct()
            .toList()
    }
}
