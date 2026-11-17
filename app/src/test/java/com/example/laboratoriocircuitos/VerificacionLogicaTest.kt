package com.example.laboratoriocircuitos

import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.GeneradorKarnaugh
import com.example.laboratoriocircuitos.logic.SimplificadorBooleano
import com.example.laboratoriocircuitos.logic.SimuladorCircuitos
import org.junit.Assert.*
import org.junit.Test

class VerificacionLogicaTest {

    private fun verificarEquivalencia(
        formulaOriginal: String,
        formulaNueva: String,
        variables: List<String>
    ) {
        val circuitoOriginal = GeneradorCircuitos.generar(formulaOriginal)
        val circuitoNuevo = GeneradorCircuitos.generar(formulaNueva)

        val combinaciones = 1 shl variables.size

        for (numero in 0 until combinaciones) {
            val entradas = variables.mapIndexed { indice, variable ->
                val desplazamiento = variables.size - indice - 1
                variable to (((numero shr desplazamiento) and 1) == 1)
            }.toMap()

            val original = SimuladorCircuitos.simular(
                circuitoOriginal,
                entradas
            ).resultadoSalida

            val nuevo = SimuladorCircuitos.simular(
                circuitoNuevo,
                entradas
            ).resultadoSalida

            assertEquals(
                "Las expresiones no coinciden para $entradas",
                original,
                nuevo
            )
        }
    }

    @Test
    fun simplificador_conservaLaFuncionOriginal() {
        val formula = "F = A*B + A*C"

        val resultado = SimplificadorBooleano.simplificar(formula)

        assertTrue(
            "El simplificador no confirmó la equivalencia",
            resultado.equivalente
        )

        verificarEquivalencia(
            formula,
            resultado.simplificada,
            resultado.variables
        )
    }

    @Test
    fun karnaugh_deDosVariables_cubreLosMinterminos() {
        verificarMapaKarnaugh("F = A*B")
    }

    @Test
    fun karnaugh_deTresVariables_cubreLosMinterminos() {
        verificarMapaKarnaugh("F = A*B + A*C")
    }

    @Test
    fun karnaugh_deCuatroVariables_cubreLosMinterminos() {
        verificarMapaKarnaugh("F = A*B + C*D")
    }

    @Test
    fun karnaugh_verificaCasosDeSalidaConstante() {
        verificarMapaKarnaugh("F = A*B*0")
        verificarMapaKarnaugh(
            "F = A*B + A*B' + A'*B + A'*B'"
        )
    }

    private fun verificarMapaKarnaugh(formula: String) {
        val resultado = GeneradorKarnaugh.generar(formula)

        val cantidadCombinaciones = 1 shl resultado.variables.size

        // El mapa debe contener todas las combinaciones.
        assertEquals(
            (0 until cantidadCombinaciones).toSet(),
            resultado.mapa.flatten().toSet()
        )

        // Los grupos no deben cubrir celdas donde la salida sea cero.
        val celdasCubiertas = resultado.grupos.flatMap { grupo ->
            val valoresGrupo = grupo.celdas.map { celda ->
                val fila = celda.first
                val columna = celda.second

                assertTrue(fila in resultado.mapa.indices)
                assertTrue(columna in resultado.mapa[fila].indices)

                resultado.mapa[fila][columna]
            }

            assertEquals(
                grupo.minterminos.toSet(),
                valoresGrupo.toSet()
            )

            valoresGrupo
        }

        assertTrue(
            "Un grupo cubre una combinación cuya salida es cero",
            celdasCubiertas.all {
                it in resultado.minterminos
            }
        )

        // Todos los minterminos deben quedar cubiertos.
        assertEquals(
            resultado.minterminos.toSet(),
            celdasCubiertas.toSet()
        )

        // La expresión simplificada debe ser equivalente a la original.
        verificarEquivalencia(
            formula,
            resultado.formulaSimplificada,
            resultado.variables
        )
    }
}
