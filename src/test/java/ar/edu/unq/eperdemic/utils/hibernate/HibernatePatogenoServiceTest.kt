package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import mongoDb.EventoMongoDao
import mongoDb.MongoEventoDAO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HibernatePatogenoServiceTest {

    val eventoDao: MongoEventoDAO = EventoMongoDao()
    private val patogenodao: PatogenoDAO = HibernatePatogenoDAO()
    private val especieDAO : EspecieDAO = HibernateEspecieDAO()
    private val ubicacionDAO : UbicacionDAO = HibernateUbicacionDAO()
    private val vectorDAO : VectorDAO = HibernateVectorDAO()
    private val vectorService : VectorService = VectorServiceImpl(vectorDAO,eventoDao,ubicacionDAO)
    private val patogenoService = PatogenoServiceImpl(patogenodao,especieDAO,ubicacionDAO,eventoDao)
    private var patogeno = Patogeno("virus")
    private val dataService:DataService=DataServiceHibernate()

    @Before
    fun crear(){
        dataService.crearSetDeDatosIniciales()
    }


    @After
    fun cleanUp(){
        dataService.eliminarTodo()
    }


    @Test
    fun seCreaYSeRecuperaUnPatogeno() {
        var idCreado = patogenoService.crearPatogeno(patogeno)
        var patogenoRecuperado = patogenoService.recuperarPatogeno(3)
        Assert.assertEquals(patogenoRecuperado.id,idCreado)
    }

    @Test
    fun seRecuperaTodosLosPatogenos(){
        patogenoService.crearPatogeno(patogeno)
        var lsPatogenos = patogenoService.recuperarATodosLosPatogenos()
        Assert.assertEquals(3,lsPatogenos.size)
    }

    @Test
    fun seAgregaUnaEspecieAlPatogeno(){
        var especieCreada = patogenoService.agregarEspecie(1,"especie1","China")
        var patogenoRecuperado = patogenoService.recuperarPatogeno(1)
        Assert.assertEquals("especie1",especieCreada.nombre)
        Assert.assertEquals(4,patogenoRecuperado.especies.size)
    }


    @Test
    fun sePuedeSaberLaCantidadDeInfectadosDeVectoresInfectadosPorUnaEspecie(){
        val cantInfectados = patogenoService.cantidadDeInfectados(5)
        Assert.assertEquals(1,cantInfectados)
        }



    @Test
    fun unaEspecieNoTieneNingunVector(){
        patogenoService.crearPatogeno(patogeno)
        val especie = Especie(patogeno, "dengue", "brasil")
        runTrx{especieDAO.guardar(especie)}
        val v1 = Vector(VectorFrontendDTO.TipoDeVector.Insecto,"madrid")
        vectorService.crearVector(v1)
        val cantInfectados = patogenoService.cantidadDeInfectados(especie.id!!)
        Assert.assertEquals(0,cantInfectados)
    }

    @Test
    fun unaEspecieEsPandemia(){
        var espRec = runTrx { especieDAO.recuperar(2) }
        var personaEnf2 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"moscu")
        personaEnf2.agregarEspecie(espRec)
        runTrx {vectorDAO.crearVector(personaEnf2)}
        var animalEnf2 = Vector(VectorFrontendDTO.TipoDeVector.Animal,"tokio")
        animalEnf2.agregarEspecie(espRec)
        runTrx {vectorDAO.crearVector(animalEnf2)}
        Assert.assertTrue(patogenoService.esPandemia(2))
    }

    @Test
    fun unaEspecieNoEsPandemia() {
        Assert.assertFalse(patogenoService.esPandemia(1))
    }


}