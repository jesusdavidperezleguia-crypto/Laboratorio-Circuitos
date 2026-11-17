package com.example.laboratoriocircuitos.logic

enum class TipoComponenteEditor(
    val nombre: String,
    val cantidadEntradas: Int
) {
    ENTRADA("Entrada", 0),
    AND("AND", 2),
    OR("OR", 2),
    NOT("NOT", 1),
    FLIP_FLOP_JK("Flip-Flop JK", 2),
    FLIP_FLOP_D("Flip-Flop D", 1),
    RELOJ("Reloj", 0),
    LED("LED", 1)
}

data class ComponenteEditor(
    val id: String,
    val tipo: TipoComponenteEditor,
    val x: Float = 0f,
    val y: Float = 0f,
    val valor: Boolean = false,
    val estado: Boolean = false
)

data class ConexionEditor(
    val origenId: String,
    val destinoId: String,
    val entradaDestino: Int
)

data class ResultadoEditor(
    val salidas: Map<String, Boolean>,
    val leds: Map<String, Boolean>
)

object MotorEditorCircuitos {

    fun evaluar(
        componentes: List<ComponenteEditor>,
        conexiones: List<ConexionEditor>
    ): ResultadoEditor {

        val componentesPorId = componentes.associateBy { it.id }

        require(componentesPorId.size == componentes.size) {
            "Hay componentes con identificadores repetidos."
        }

        conexiones.forEach { conexion ->

            val origen = componentesPorId[conexion.origenId]
                ?: error("No existe el componente de origen.")

            val destino = componentesPorId[conexion.destinoId]
                ?: error("No existe el componente de destino.")

            require(
                conexion.entradaDestino in
                    0 until destino.tipo.cantidadEntradas
            ) {
                "La conexión apunta a una entrada que no existe."
            }
        }

        require(
            conexiones.map {
                it.destinoId to it.entradaDestino
            }.distinct().size == conexiones.size
        ) {
            "Una entrada no puede tener dos cables."
        }

        val memoria = mutableMapOf<String, Boolean>()

        fun leerSalida(
            id: String,
            visitando: MutableSet<String>
        ): Boolean {

            memoria[id]?.let { return it }

            require(visitando.add(id)) {
                "Se detectó una conexión circular en el circuito."
            }

            val componente = componentesPorId[id]
                ?: error("Componente desconocido: $id")

            fun entrada(numero: Int): Boolean {

                val cable = conexiones.firstOrNull {
                    it.destinoId == id &&
                        it.entradaDestino == numero
                }

                return cable?.let {
                    leerSalida(it.origenId, visitando)
                } ?: false
            }

            val resultado = when (componente.tipo) {

                TipoComponenteEditor.ENTRADA,
                TipoComponenteEditor.RELOJ ->
                    componente.valor

                TipoComponenteEditor.AND ->
                    entrada(0) && entrada(1)

                TipoComponenteEditor.OR ->
                    entrada(0) || entrada(1)

                TipoComponenteEditor.NOT ->
                    !entrada(0)

                TipoComponenteEditor.FLIP_FLOP_JK,
                TipoComponenteEditor.FLIP_FLOP_D ->
                    componente.estado

                TipoComponenteEditor.LED ->
                    entrada(0)
            }

            visitando.remove(id)
            memoria[id] = resultado

            return resultado
        }

        val salidas = componentes.associate {
            it.id to leerSalida(it.id, mutableSetOf())
        }

        val leds = componentes
            .filter { it.tipo == TipoComponenteEditor.LED }
            .associate { it.id to salidas.getValue(it.id) }

        return ResultadoEditor(
            salidas = salidas,
            leds = leds
        )
    }

    fun aplicarPulso(
        componentes: List<ComponenteEditor>,
        conexiones: List<ConexionEditor>
    ): List<ComponenteEditor> {

        val resultadoActual = evaluar(
            componentes,
            conexiones
        )

        fun leerEntrada(
            componente: ComponenteEditor,
            numero: Int
        ): Boolean {

            val cable = conexiones.firstOrNull {
                it.destinoId == componente.id &&
                    it.entradaDestino == numero
            }

            return cable?.let {
                resultadoActual.salidas[it.origenId] ?: false
            } ?: false
        }

        return componentes.map { componente ->

            when (componente.tipo) {

                TipoComponenteEditor.FLIP_FLOP_D -> {
                    componente.copy(
                        estado = leerEntrada(componente, 0)
                    )
                }

                TipoComponenteEditor.FLIP_FLOP_JK -> {

                    val j = leerEntrada(componente, 0)
                    val k = leerEntrada(componente, 1)

                    val nuevoEstado = when {
                        !j && !k -> componente.estado
                        !j && k -> false
                        j && !k -> true
                        else -> !componente.estado
                    }

                    componente.copy(estado = nuevoEstado)
                }

                else -> componente
            }
        }
    }
}
