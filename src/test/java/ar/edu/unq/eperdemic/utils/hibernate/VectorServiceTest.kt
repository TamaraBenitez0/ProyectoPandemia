package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.AtributoPatogeno
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import exceptions.ErrorContagio
import exceptions.ErrorRecuperar
import mongoDb.EventoMongoDao
import mongoDb.MongoEventoDAO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class VectorServiceTest {


    val dataService:DataService=DataServiceHibernate()
    val especieDao:EspecieDAO =HibernateEspecieDAO()
    val eventoDao: MongoEventoDAO = EventoMongoDao()
    val ubicacionDao: UbicacionDAO = HibernateUbicacionDAO()
    private val dao: VectorDAO = HibernateVectorDAO()
    private val vectorService = VectorServiceImpl(dao,eventoDao,ubicacionDao)
    private val daoPat: PatogenoDAO = HibernatePatogenoDAO()
    lateinit var vec: Vector
    private val atribSumarDao:AtributoPatogenoDAO =HibernateAtributoPatogenoDao()



    @Before
    fun setUp() {
        dataService.crearSetDeDatosIniciales()
    }


    @Test
    fun seCreaYSeRecuperaUnVector() {

        var v = Vector(VectorFrontendDTO.TipoDeVector.Insecto, "buenos aires")
        vectorService.crearVector(v)
        var recuperado = vectorService.recuperarVector(2)
        Assert.assertTrue(recuperado.esInsecto())

    }

    @Test
    fun seActualizaUnVector() {
        //un test que en la vida real no tiene sentido, utilizado solo para probar la actualizacion del vector
        var v: Vector = Vector(VectorFrontendDTO.TipoDeVector.Persona,"pekin")
        vectorService.crearVector(v)
        v.setId(1)
        v.tipo = VectorFrontendDTO.TipoDeVector.Animal
        vectorService.actualizarVector(v)

        Assert.assertTrue(vectorService.recuperarVector(1).esAnimal())
    }

    @Test(expected= ErrorRecuperar::class)
    fun seEliminaUnVector(){
        //el ultimo vector es un insecto del set
        var v = Vector(VectorFrontendDTO.TipoDeVector.Animal, "moscu")
        vectorService.crearVector(v)
        Assert.assertTrue(vectorService.recuperarVector(9).esAnimal())
        vectorService.borrarVector(9)
        Assert.assertTrue(vectorService.recuperarVector(9).esInsecto()) // aca levanta la excepcion

    }

    @Test
    fun testUnHumanoContagiaAOtroHumano() {
        var v1 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        var v2 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        var atrib2= AtributoPatogeno()
        runTrx { atribSumarDao.guardar(atrib2)}
        var pat = Patogeno("virus")
        pat.setAts(atrib2)
        pat.atributo!!.capacidadContagioPersona = 100
        runTrx{daoPat.crear(pat)}
        var especie = Especie(pat, "covid", "argentina")
        runTrx{especieDao.guardar(especie)}
        vectorService.crearVector(v1)
        vectorService.infectar(v1,especie)
        vectorService.crearVector(v2)
        vectorService.contagiar(v1, listOf(v2))
        Assert.assertTrue(vectorService.recuperarVector(1).estaInfectado())
        Assert.assertEquals(1, vectorService.recuperarVector(2).especies.size)
    }

    @Test(expected= ErrorContagio::class)
    fun testUnHumanoSanoNoContagiaYLevantaUnaExcepcion() {
        var v1 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        var v2 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        var atrib2= AtributoPatogeno()
        atrib2.capacidadContagioPersona = 100
        runTrx { atribSumarDao.guardar(atrib2)}
        var pat = Patogeno("virus")
        pat.setAts(atrib2)
        runTrx{daoPat.crear(pat)}
        var especie = Especie(pat, "covid", "argentina")
        runTrx{especieDao.guardar(especie)}
        vectorService.crearVector(v1)
        vectorService.crearVector(v2)
        vectorService.contagiar(v1, listOf(v2))
        Assert.assertTrue(vectorService.recuperarVector(1).estaInfectado())
        Assert.assertEquals(1, vectorService.recuperarVector(2).especies.size)
    }

    @Test

    fun testUnHumanoIntentaContagiarAUnAnimalYNoPuede() {
        var v1 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"pekin")
        var v2 = Vector(VectorFrontendDTO.TipoDeVector.Animal,"pekin")
        var atrib2= AtributoPatogeno()
        atrib2.capacidadContagioPersona = 100
        runTrx { atribSumarDao.guardar(atrib2)}
        var pat = Patogeno("virus")
        pat.setAts(atrib2)
        runTrx { daoPat.crear(pat) }
        var especie = Especie(pat, "covid", "argentina")
        v1.agregarEspecie(especie)
        vectorService.crearVector(v2)
        v1.contagiar(listOf(v2))
        Assert.assertEquals(0, v2.especies.size)
    }

    @Test
    fun testSeInfectaAUnVectorConLaEspecie(){
        val v1 = Vector(VectorFrontendDTO.TipoDeVector.Insecto,"madrid")
        vectorService.crearVector(v1)
        Assert.assertFalse(v1.estaInfectado())
        var especie= runTrx { especieDao.recuperar(2) }
        vectorService.infectar(v1,especie)
        val vecDB = vectorService.recuperarVector(8)
        Assert.assertTrue(vecDB.estaInfectado())
    }

    @Test

    fun testSeObtieneLasEnfermedadesDeUnVector(){
        Assert.assertEquals(vectorService.enfermedades(6).size,0)
        Assert.assertEquals(vectorService.enfermedades(1).size,1)

    }

    @Test
    fun unaEspecieAlConseguirCincoInfectadosAdquiereUnPuntoDeAdn() {
        var especie = runTrx {especieDao.recuperar(2)}
        var v1 = Vector(VectorFrontendDTO.TipoDeVector.Insecto,"pekin")
        var v2 = Vector(VectorFrontendDTO.TipoDeVector.Animal,"pekin")
        var v3 = Vector(VectorFrontendDTO.TipoDeVector.Insecto,"pekin")
        vectorService.crearVector(v1)
        vectorService.crearVector(v2)
        vectorService.crearVector(v3)
        vectorService.infectar(v1,especie)
        vectorService.infectar(v2,especie)
        vectorService.infectar(v3,especie)
        Assert.assertEquals(1,especie.adn)

    }


    @After
    fun cleanUp(){
        dataService.eliminarTodo()
    }
}