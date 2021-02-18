package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.ProbabilidadDeContagio
import ar.edu.unq.eperdemic.modelo.Vector
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class VectorTest {

    lateinit var v1 :Vector
    lateinit var v2 :Vector
    lateinit var v3 :Vector
    lateinit var v4 :Vector
    lateinit var v5 :Vector
    lateinit var pat :Patogeno
    lateinit var especie:Especie
    lateinit var probabilidad:ProbabilidadDeContagio



    @Before
    fun setUp(){
        v1= Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        v1.id=1
        v2= Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        v2.id=2
        v3= Vector(VectorFrontendDTO.TipoDeVector.Animal,"buenos aires")
        v3.id=3
        v4= Vector(VectorFrontendDTO.TipoDeVector.Insecto,"buenos aires")
        v4.id=4
        v5= Vector(VectorFrontendDTO.TipoDeVector.Insecto,"buenos aires")
        v5.id=5
        pat= Patogeno("virus")
        especie= Especie(pat,"covid","argentina")
        probabilidad= Mockito.mock(ProbabilidadDeContagio::class.java)
    }

    @Test
    fun unVectorContagiaAAotroVector() {
        Mockito.`when`(probabilidad.getResultadoDeContagio(100)).thenReturn(true)
        Mockito.`when`(probabilidad.calcularFactorContagio(especie,v1)).thenReturn(100)
        v1.agregarEspecie(especie)
        v1.setProbabilidad(probabilidad)
        v1.contagiar(listOf(v2))
        Assert.assertEquals(1,v2.especies.size)
    }

    @Test
    fun unVectorNoPuedeContagiaAAotroVector() {
        Mockito.`when`(probabilidad.getResultadoDeContagio(100)).thenReturn(false)
        Mockito.`when`(probabilidad.calcularFactorContagio(especie,v1)).thenReturn(100)
        v1.agregarEspecie(especie)
        v1.setProbabilidad(probabilidad)
        v1.contagiar(listOf(v2))
        Assert.assertEquals(0,v2.especies.size)
    }

    @Test
    fun unVectorPersonaNoPuedeContagiaAUnVectorAnimal() {
        Mockito.`when`(probabilidad.getResultadoDeContagio(100)).thenReturn(true)
        Mockito.`when`(probabilidad.calcularFactorContagio(especie,v1)).thenReturn(100)
        v1.agregarEspecie(especie)
        v1.setProbabilidad(probabilidad)
        v1.contagiar(listOf(v3))
        Assert.assertEquals(0,v3.especies.size)
    }

    @Test
    fun unVectorSanoNoPuedeContagiarAOtroVector() {
        Mockito.`when`(probabilidad.getResultadoDeContagio(100)).thenReturn(true)
        Mockito.`when`(probabilidad.calcularFactorContagio(especie,v1)).thenReturn(100)
        v1.setProbabilidad(probabilidad)
        v1.contagiar(listOf(v2))
        Assert.assertEquals(0,v2.especies.size)
    }

    @Test
    fun unVectorInsectoNoPuedeContagiarOtroVectorInsecto() {
        Mockito.`when`(probabilidad.getResultadoDeContagio(100)).thenReturn(true)
        Mockito.`when`(probabilidad.calcularFactorContagio(especie,v1)).thenReturn(100)
        v4.agregarEspecie(especie)
        v4.setProbabilidad(probabilidad)
        v4.contagiar(listOf(v5))
        Assert.assertEquals(0,v5.especies.size)
    }

    @Test
    fun unVectorAnimalSoloPuedeSerContagiadoPorUnInsecto() {
        Mockito.`when`(probabilidad.getResultadoDeContagio(100)).thenReturn(true)
        Mockito.`when`(probabilidad.calcularFactorContagio(especie,v4)).thenReturn(100)
        v4.agregarEspecie(especie)
        v4.setProbabilidad(probabilidad)
        v4.contagiar(listOf(v3))
        Assert.assertEquals(1,v3.especies.size)
    }

}