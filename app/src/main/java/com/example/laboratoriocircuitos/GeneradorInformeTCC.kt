package com.example.laboratoriocircuitos

import com.example.laboratoriocircuitos.logic.ComparadorCircuitos
import com.example.laboratoriocircuitos.logic.ConversorNumeracion
import com.example.laboratoriocircuitos.logic.GeneradorKarnaugh
import com.example.laboratoriocircuitos.logic.GeneradorTablaVerdad
import com.example.laboratoriocircuitos.logic.SimplificadorBooleano
import java.util.Locale

fun construirResumenCompletoTCC(): String {

    val numeracion = ConversorNumeracion.convertir(25)

    return buildString {

        appendLine("TRABAJO CONTEXTUALIZADO DEL CURSO")
        appendLine("SISTEMA DIGITAL DE CONTROL DE ACCESO INDUSTRIAL")
        appendLine("Caso de estudio: SmartIndustries Caribe S.A.")
        appendLine()

        appendLine("FASE 1. ANALISIS DE REQUISITOS Y SISTEMAS NUMERICOS")
        appendLine()

        appendLine("1.1 Contexto del problema")
        appendLine(
            "Se plantea un sistema digital de control de acceso y seguridad " +
            "para un entorno industrial. El sistema recibe señales de " +
            "identificacion, autenticacion y sensores, y genera salidas " +
            "para controlar el acceso, activar alarmas y registrar eventos."
        )
        appendLine()

        appendLine("1.2 Requisitos funcionales")
        appendLine("- Validar la tarjeta de acceso.")
        appendLine("- Comprobar el codigo PIN.")
        appendLine("- Verificar el estado de la puerta.")
        appendLine("- Detectar una parada de emergencia.")
        appendLine("- Supervisar el paso mediante un sensor optico.")
        appendLine("- Detectar presencia mediante proximidad.")
        appendLine("- Activar las salidas de alarma, apertura y registro.")
        appendLine()

        appendLine("1.3 Identificacion de entradas")
        appendLine("A: Tarjeta autorizada.")
        appendLine("B: PIN correcto.")
        appendLine("C: Puerta cerrada.")
        appendLine("D: Parada de emergencia activada.")
        appendLine("E: Paso optico despejado.")
        appendLine("F: Persona detectada por proximidad.")
        appendLine()

        appendLine("1.4 Identificacion de salidas")
        appendLine("AL: Alarma de seguridad.")
        appendLine("AP: Apertura de puerta.")
        appendLine("R: Registro de acceso validado.")
        appendLine()

        appendLine("1.5 Conversion entre sistemas numericos")
        appendLine(
            "La aplicacion permite convertir identificaciones " +
            "decimales entre 0 y 9999 a representacion binaria y BCD."
        )
        appendLine("Valor de demostracion configurado: 25 decimal.")
        appendLine("Decimal: ${numeracion.decimal}")
        appendLine("Binario: ${numeracion.binario}")
        appendLine("BCD: ${numeracion.bcd}")
        appendLine(
            "Nota: 25 es el valor inicial de demostracion de la aplicacion; " +
            "no representa una identificacion real asignada al equipo."
        )
        appendLine()

        appendLine("FASE 2. LOGICA DIGITAL Y ALGEBRA BOOLEANA")
        appendLine()

        appendLine("2.1 Definicion de las funciones")
        appendLine("ALARMA: AL = D + !E")
        appendLine(
            "La alarma se activa cuando se activa la emergencia " +
            "o cuando el paso optico no esta despejado."
        )
        appendLine()

        appendLine("APERTURA: AP = A*B*C*!D*E*F")
        appendLine(
            "La apertura requiere tarjeta autorizada, PIN correcto, " +
            "puerta cerrada, emergencia desactivada, paso despejado " +
            "y presencia detectada."
        )
        appendLine()

        appendLine("REGISTRO: R = A*B")
        appendLine(
            "El registro de acceso validado se activa cuando " +
            "la tarjeta y el PIN son correctos."
        )
        appendLine()

        appendLine("2.2 Operadores utilizados")
        appendLine("+: operador OR (suma logica).")
        appendLine("*: operador AND (producto logico).")
        appendLine("!: operador NOT (negacion).")
        appendLine()

        appendLine("2.3 Compuertas NAND y NOR")
        appendLine("La aplicacion tambien admite las compuertas NAND y NOR para el diseno y simulacion de circuitos digitales.")
        appendLine()
        appendLine("NAND (NOT AND): simbolo ↑")
        appendLine("Expresion: A ↑ B = !(A*B)")
        appendLine("Su salida es 0 solamente cuando ambas entradas son 1.")
        appendLine()
        appendLine("NOR (NOT OR): simbolo ↓")
        appendLine("Expresion: A ↓ B = !(A+B)")
        appendLine("Su salida es 1 solamente cuando ambas entradas son 0.")
        appendLine()
        appendLine("TABLA DE VERDAD DE NAND Y NOR")
        appendLine("A | B | NAND | NOR")
        appendLine("0 | 0 |   1  |  1")
        appendLine("0 | 1 |   1  |  0")
        appendLine("1 | 0 |   1  |  0")
        appendLine("1 | 1 |   0  |  0")
        appendLine()
        appendLine("Las compuertas NAND y NOR son compuertas universales, ya que permiten construir otras funciones logicas mediante combinaciones de ellas.")
        appendLine()

        appendLine("FASE 3. SIMPLIFICACION Y MAPAS DE KARNAUGH")
        appendLine()
        appendLine(
            "Se generan mapas de Karnaugh independientes para cada salida. " +
            "Las tablas de verdad se calculan evaluando todas las " +
            "combinaciones posibles de sus variables."
        )
        appendLine()

        agregarAnalisisFuncion(
            this,
            "ALARMA DE SEGURIDAD",
            "AL = D+!E",
            "La salida se activa ante emergencia o paso optico obstruido."
        )

        agregarAnalisisFuncion(
            this,
            "APERTURA DE PUERTA",
            "AP = A*B*C*!D*E*F",
            "La salida requiere que se cumplan las seis condiciones de acceso."
        )

        agregarAnalisisFuncion(
            this,
            "REGISTRO DE ACCESO",
            "R = A*B",
            "La salida se activa al validar tarjeta y PIN."
        )

        appendLine("VALIDACION GENERAL")
        appendLine(
            "Las expresiones originales y simplificadas se comparan " +
            "mediante sus tablas de verdad. La equivalencia se verifica " +
            "cuando ambas producen la misma salida para todas las entradas."
        )
        appendLine()

        val ejemploDidactico = SimplificadorBooleano.simplificar("F = A+A*B")

        appendLine("FASE 4. EJEMPLO DIDACTICO DE SIMPLIFICACION")
        appendLine()
        appendLine("4.1 Funcion original")
        appendLine("F = A + A*B")
        appendLine("Variables: ${ejemploDidactico.variables.joinToString(", ")}")
        appendLine()
        appendLine("4.2 Desarrollo paso a paso")

        if (ejemploDidactico.pasos.isEmpty()) {
            appendLine("El simplificador no registro pasos intermedios.")
        } else {
            ejemploDidactico.pasos.forEachIndexed { indice, paso ->
                appendLine("Paso ${indice + 1}: ${paso.regla}")
                appendLine("Antes: ${paso.antes}")
                appendLine("Despues: ${paso.despues}")
                appendLine("Explicacion: ${paso.explicacion}")
                appendLine()
            }
        }

        appendLine("4.3 Resultado simplificado")
        appendLine("F = ${ejemploDidactico.simplificada}")
        appendLine()
        appendLine("4.4 Verificacion de equivalencia")
        appendLine(
            "Equivalencia: ${
                if (ejemploDidactico.equivalente) "VERIFICADA" else "NO VERIFICADA"
            }"
        )
        appendLine(
            "La verificacion compara las salidas de la funcion original y la simplificada."
        )
        appendLine()
        appendLine("ALCANCE Y LIMITACIONES")
        appendLine(
            "El conteo de complejidad presentado es una estimacion " +
            "de nodos logicos de las expresiones. No constituye un " +
            "conteo fisico definitivo de compuertas ni considera " +
            "necesariamente optimizaciones de implementacion."
        )
        appendLine(
            "La salida R representa una condicion logica de registro " +
            "validado. No demuestra almacenamiento persistente de eventos."
        )
        appendLine(
            "La fase de circuitos secuenciales se encuentra implementada " +
            "mediante contadores modulo 6, modulo 9 y 7490, ademas de un " +
            "editor educativo con entradas, compuertas, reloj, LED y " +
            "flip-flops tipo D y JK."
        )
        appendLine(
            "El simulador permite aplicar pulsos de reloj y observar " +
            "los cambios de estado de los elementos de memoria."
        )
        appendLine(
            "Los resultados corresponden a simulaciones logicas del " +
            "software. No representan mediciones de un circuito fisico " +
            "ni validacion de componentes electronicos reales."
        )
        appendLine(
            "El registro persistente de eventos industriales y la " +
            "integracion con sensores y actuadores fisicos no forman " +
            "parte de las funciones verificadas en esta version."
        )
    }
}

private fun agregarAnalisisFuncion(
    destino: StringBuilder,
    nombre: String,
    formula: String,
    descripcion: String
) {
    val karnaugh = GeneradorKarnaugh.generar(formula)
    val tabla = GeneradorTablaVerdad.generar(formula)

    val formulaSimplificada =
        formula.substringBefore("=") + " = " + karnaugh.formulaSimplificada

    val comparacion = ComparadorCircuitos.comparar(
        formulaOriginal = formula,
        formulaSimplificada = formulaSimplificada
    )

    with(destino) {

        appendLine("========================================")
        appendLine(nombre)
        appendLine("========================================")
        appendLine("Funcion original: $formula")
        appendLine("Descripcion: $descripcion")
        appendLine("Variables: ${karnaugh.variables.joinToString(", ")}")
        appendLine("Cantidad de variables: ${karnaugh.variables.size}")
        appendLine("Combinaciones: ${tabla.filas.size}")
        appendLine()

        appendLine("MINTERMINOS")
        appendLine(
            karnaugh.minterminos.joinToString(", ")
                .ifBlank { "No hay minterminos activos." }
        )
        appendLine()

        appendLine("MAPA DE KARNAUGH")
        appendLine("Las celdas muestran los valores logicos 0 y 1.")
        appendLine(
            "Fila/Col | ${karnaugh.columnasGray.joinToString(" | ")}"
        )

        karnaugh.mapa.forEachIndexed { indice, fila ->
            val valoresLogicos = fila.map { mintermino ->
                if (mintermino in karnaugh.minterminos) "1" else "0"
            }

            appendLine(
                "${karnaugh.filasGray[indice]} | ${valoresLogicos.joinToString(" | ")}"
            )
        }

        appendLine()
        appendLine("AGRUPACIONES")

        if (karnaugh.grupos.isEmpty()) {
            appendLine("No se requieren agrupaciones.")
        } else {
            karnaugh.grupos.forEachIndexed { indice, grupo ->
                appendLine("Grupo ${indice + 1}")
                appendLine("Patron: ${grupo.patron}")
                appendLine("Expresion: ${grupo.expresion}")
                appendLine(
                    "Minterminos: ${grupo.minterminos.joinToString(", ")}"
                )
                appendLine("Celdas: ${grupo.celdas.joinToString(", ")}")
            }
        }

        appendLine()
        appendLine("FUNCION SIMPLIFICADA")
        appendLine(formulaSimplificada)
        appendLine()

        appendLine("COMPARACION DE COMPLEJIDAD")
        appendLine(
            "Nodos estimados originales: ${comparacion.compuertasOriginales}"
        )
        appendLine(
            "Nodos estimados simplificados: ${comparacion.compuertasSimplificadas}"
        )
        appendLine("Nodos reducidos: ${comparacion.compuertasReducidas}")
        appendLine(
            "Reduccion estimada: ${
                String.format(
                    Locale.US,
                    "%.2f",
                    comparacion.porcentajeReduccion
                )
            } %"
        )
        appendLine(
            "Equivalencia: ${
                if (comparacion.equivalentes) "VERIFICADA" else "NO VERIFICADA"
            }"
        )
        appendLine("Detalle: ${comparacion.mensaje}")
        appendLine()

        appendLine("TABLA DE VERDAD")
        appendLine(
            (tabla.variables + "SALIDA").joinToString(" | ")
        )

        tabla.filas.forEach { fila ->
            val valores = tabla.variables.map { variable ->
                if (fila.entradas[variable] == true) "1" else "0"
            }

            appendLine(
                (valores + if (fila.salida) "1" else "0")
                    .joinToString(" | ")
            )
        }

        appendLine()
    }
}
