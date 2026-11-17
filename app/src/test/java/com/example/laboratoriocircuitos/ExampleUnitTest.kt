package com.example.laboratoriocircuitos

import com.example.laboratoriocircuitos.logic.GeneradorBooleano
import com.example.laboratoriocircuitos.logic.GeneradorCircuitos
import com.example.laboratoriocircuitos.logic.GeneradorTablaVerdad
import com.example.laboratoriocircuitos.logic.SimuladorCircuitos
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    @Test
    fun compuertaAND_funcionaCorrectamente() {
        assertTrue(
            GeneradorBooleano.evaluar(
                "F = A*B",
                mapOf("A" to true, "B" to true)
            )
        )

        assertFalse(
            GeneradorBooleano.evaluar(
                "F = A*B",
                mapOf("A" to true, "B" to false)
            )
        )
    }

    @Test
    fun compuertaOR_funcionaCorrectamente() {
        assertTrue(
            GeneradorBooleano.evaluar(
                "F = A+B",
                mapOf("A" to false, "B" to true)
            )
        )

        assertFalse(
            GeneradorBooleano.evaluar(
                "F = A+B",
                mapOf("A" to false, "B" to false)
            )
        )
    }

    @Test
    fun compuertaNOT_funcionaCorrectamente() {
        assertTrue(
            GeneradorBooleano.evaluar(
                "F = !A",
                mapOf("A" to false)
            )
        )

        assertFalse(
            GeneradorBooleano.evaluar(
                "F = !A",
                mapOf("A" to true)
            )
        )
    }

    @Test
    fun parentesis_y_precedencia_seRespetan() {
        val resultado = GeneradorBooleano.evaluar(
            "F = (A+B)*C",
            mapOf(
                "A" to true,
                "B" to false,
                "C" to false
            )
        )

        assertFalse(resultado)

        val segundoResultado = GeneradorBooleano.evaluar(
            "F = (A+B)*C",
            mapOf(
                "A" to true,
                "B" to false,
                "C" to true
            )
        )

        assertTrue(segundoResultado)
    }

    @Test
    fun tablaDeVerdad_generaOchoCombinaciones() {
        val tabla = GeneradorTablaVerdad.generar(
            "F = A*B + A*C"
        )

        assertEquals(3, tabla.variables.size)
        assertEquals(8, tabla.filas.size)

        tabla.filas.forEach { fila ->
            val a = fila.entradas["A"] == true
            val b = fila.entradas["B"] == true
            val c = fila.entradas["C"] == true

            val esperado = (a && b) || (a && c)

            assertEquals(esperado, fila.salida)
        }
    }

    @Test
    fun circuito_y_tablaDeVerdad_producenElMismoResultado() {
        val formula = "F = A*B + A*C"

        val circuito = GeneradorCircuitos.generar(formula)
        val tabla = GeneradorTablaVerdad.generar(formula)

        assertEquals(8, tabla.filas.size)

        tabla.filas.forEach { fila ->
            val simulacion = SimuladorCircuitos.simular(
                circuito,
                fila.entradas
            )

            assertEquals(
                "Diferencia encontrada para las entradas ${fila.entradas}",
                fila.salida,
                simulacion.resultadoSalida
            )
        }
    }
}
