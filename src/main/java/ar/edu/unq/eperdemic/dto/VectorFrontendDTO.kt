package ar.edu.unq.eperdemic.dto

import ar.edu.unq.eperdemic.modelo.Vector

class VectorFrontendDTO(val tipoDeVector : TipoDeVector,
                        val nombreDeUbicacionPresente: String) {

    enum class TipoDeVector {
        Persona, Insecto, Animal
    }

    fun aModelo() : Vector {
        return Vector(tipoDeVector,nombreDeUbicacionPresente)
    }

}