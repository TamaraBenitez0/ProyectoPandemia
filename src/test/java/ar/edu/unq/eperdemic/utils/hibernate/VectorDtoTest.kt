package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Vector
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorDtoTest {

    lateinit var dto:VectorFrontendDTO
    @Before
    fun setUp(){
        dto= VectorFrontendDTO(VectorFrontendDTO.TipoDeVector.Persona,"quilmes")
    }

    @Test
    fun sePruebaQueAModeloCreaUnVector(){//test prueba
       var vector: Vector = dto.aModelo() //aca se comprueba que genera un vector
        Assert.assertEquals("quilmes",vector.lugar!!.nombreUbicacion)
    }
}