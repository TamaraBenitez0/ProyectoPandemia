package ar.edu.unq.eperdemic.utils.hibernate

import InfluxDB.GenericCurvaDAO
import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import exceptions.UbicacionMuyLejana
import exceptions.UbicacionNoAlcanzable
import mongoDb.EventoMongoDao
import mongoDb.MongoEventoDAO
import neo4j.UbicacionNeo4jDAO
import neo4j.UbicacionNeoDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UbicacionServiceTest {
    var curva :GenericCurvaDAO = GenericCurvaDAO() //por ahora uso la clase concreta para testear
    var dataService: DataService =DataServiceHibernate()
    val eventoDao: MongoEventoDAO = EventoMongoDao()
    var vectorD: VectorDAO = HibernateVectorDAO()
    var ubicacionD: UbicacionDAO = HibernateUbicacionDAO()
    var ubicacionNeoDao: UbicacionNeoDao = UbicacionNeo4jDAO()
    var service: UbicacionService = UbicacionServiceImpl(vectorD,ubicacionD,ubicacionNeoDao,eventoDao,curva)
    var patogenoDao:PatogenoDAO=HibernatePatogenoDAO()
    var especieDao: EspecieDAO =HibernateEspecieDAO()


    @Before
    fun setUp(){
        dataService.crearSetDeDatosIniciales()
    }

    @Test
    fun seCreaUnaUbicacionYSeRecuperaUnaUbicacion(){


        service.crearUbicacion("Finlandia")
        Assert.assertEquals("Finlandia",service.recuperarUbicacion("Finlandia").nombreUbicacion)

    }

    @Test
    fun seCreaUnaUbicacionEnNeo(){

        service.crearUbicacion("Italia")
        Assert.assertTrue(ubicacionNeoDao.existeUbicacion("Italia"))
        //utilizo el neo dao para verificar que se haya creado la ubicacion
    }

    @Test
    fun seConectaUnaUbicacion() {

        service.crearUbicacion("Italia")
        service.crearUbicacion("Francia")
        service.conectar("Italia","Francia","Maritimo")
        Assert.assertTrue(ubicacionNeoDao.existeRelacion("Italia","Francia","Maritimo"))
        //utilizo el neo dao para verificar que se haya conectado la ubicacion con otra.
    }


    @Test
    fun seActualizaUnaUbicacion(){

        var vec: Vector= Vector()
        service.crearUbicacion("Cordoba")
        var recuperada=service.recuperarUbicacion("Cordoba")
        recuperada.agregarVector(vec)
        service.actualizarUbicacion(recuperada)

        Assert.assertEquals(1,recuperada.vectores.size)

    }

    @Test
    fun unVectorSeMueveAOtraUbicacion(){
        service.conectar("pekin","buenos aires","Terrestre")
        service.conectar("buenos aires","pekin","Terrestre")
        service.mover(1,"pekin")
        var recu = runTrx { vectorD.recuperar(1) }
        Assert.assertEquals("pekin",recu.lugarDeResidencia())
    }

    @Test
    fun unVectorSeMueveAOtraUbicacionYContagiaAUnoSano(){

        service.conectar("tokio","buenos aires","Aereo")
        service.mover(2,"buenos aires")

        var recu = runTrx { vectorD.recuperar(1) }
        var recu2 = runTrx { vectorD.recuperar(2) }
        Assert.assertEquals("buenos aires",recu2.lugarDeResidencia())
        Assert.assertEquals(2,recu.especies.size)
        Assert.assertEquals("buenos aires",recu.lugarDeResidencia())
    }

    @Test(expected=UbicacionNoAlcanzable::class)
    fun unVectorQueSeQuiereMoverAUnaUbicacionNoConectadaEsUnaUbicacionInalcanzable(){

        service.conectar("tokio","buenos aires","Aereo")
        service.mover(1,"tokio")

        var recu = runTrx { vectorD.recuperar(1) }
        var recu2 = runTrx { vectorD.recuperar(2) }
        Assert.assertEquals("buenos aires",recu2.lugarDeResidencia())
        Assert.assertEquals(2,recu.especies.size)
        Assert.assertEquals("buenos aires",recu.lugarDeResidencia())
    }

    @Test(expected=UbicacionNoAlcanzable::class)
    fun unVectorQueSeQuiereMoverAUnaUbicacionYNoEsDelTipoDeCaminoEsInalcanzable(){

        service.conectar("buenos aires","tokio","Aereo")
        service.mover(1,"tokio")

        var recu = runTrx { vectorD.recuperar(1) }
        var recu2 = runTrx { vectorD.recuperar(2) }
        Assert.assertEquals("buenos aires",recu2.lugarDeResidencia())
        Assert.assertEquals(2,recu.especies.size)
        Assert.assertEquals("buenos aires",recu.lugarDeResidencia())
    }

    @Test(expected=UbicacionMuyLejana::class)
    fun unVectorQuiereMoverAOtraUbicacionAMasDeUnMovimientoDeDistanciaPeroEsMuyLejana(){

        service.conectar("tokio","buenos aires","Aereo")
        service.conectar("buenos aires","madrid","Aereo")

        service.mover(2,"madrid")

        var recu = runTrx { vectorD.recuperar(1) }
        var recu2 = runTrx { vectorD.recuperar(2) }
        Assert.assertEquals("buenos aires",recu2.lugarDeResidencia())
        Assert.assertEquals(2,recu.especies.size)
        Assert.assertEquals("buenos aires",recu.lugarDeResidencia())
    }

    @Test
    fun unVectorAleatorioInfectadoExpandeElVirus(){

        runTrx { vectorD.borrarVector(1);vectorD.borrarVector(3);vectorD.borrarVector(8) }
        //borro los vectores para poder testear el random de alguna manera
        service.expandir("pekin")
        var recu = runTrx { vectorD.recuperar(6) }
        Assert.assertEquals(1,recu.especies.size)
    }

    @Test

    fun getCaminoMasCortoEntreDosUbicacionesParaUnaPersona(){

        service.conectar("madrid","tokio","Terrestre")
        service.conectar("madrid","buenos aires","Aereo")
        service.conectar("buenos aires","madrid","Aereo")
        service.conectar("buenos aires","pekin","Terrestre")
        service.conectar("pekin","moscu","Terrestre")
        service.conectar("moscu","tokio","Maritimo")
        service.conectar("tokio","buenos aires","Terrestre")

        service.moverMasCorto(7,"tokio")
        var v1 = runTrx { vectorD.recuperar(7) }
        Assert.assertEquals(v1.lugar!!.nombreUbicacion,"tokio")
    }

    @Test(expected=UbicacionNoAlcanzable::class)
    fun unInsectoNoPuedeIrPorUnCaminoMaritimo(){

        service.conectar("madrid","tokio","Terrestre")
        service.conectar("madrid","buenos aires","Aereo")
        service.conectar("buenos aires","madrid","Aereo")
        service.conectar("buenos aires","pekin","Terrestre")
        service.conectar("pekin","moscu","Terrestre")
        service.conectar("moscu","tokio","Maritimo")
        service.conectar("tokio","buenos aires","Terrestre")

        service.moverMasCorto(8,"tokio")
        var v1 = runTrx { vectorD.recuperar(8) }
        Assert.assertEquals(v1.lugar!!.nombreUbicacion,"tokio")
    }


    @Test
    fun cantidadUbicacionesDistintasAlcanzables(){

        service.conectar("madrid","tokio","Aereo")
        service.conectar("madrid","pekin","Terrestre")
        service.conectar("tokio","paris","Aereo")
        service.conectar("paris","moscu","Aereo")
        service.conectar("pekin","moscu","Maritimo")

        var cantidad = service.capacidadDeExpansion(4,1)

        Assert.assertEquals(2,cantidad)
    }


    @Test
    fun sePruebaLaGeneracionDeEstadisticaDeVector(){
        service.conectar("tokio", "buenos aires","Aereo")
        service.mover(2,"buenos aires")
        var lista = listOf("pekin","tokio","buenos aires","moscu","madrid")
        service.estadisticasDeUbicaciones(lista,"dengue")
        service.estadisticasDeUbicaciones(lista,"covid")
        Thread.sleep(1000)
        val v1 = Vector(VectorFrontendDTO.TipoDeVector.Insecto,"buenos aires")
        val v2 = Vector(VectorFrontendDTO.TipoDeVector.Insecto,"buenos aires")
        runTrx {      vectorD.crearVector(v1)
            vectorD.crearVector(v2)}

        service.estadisticasDeUbicaciones(lista,"dengue")
        service.estadisticasDeUbicaciones(lista,"covid")
        Thread.sleep(1000)
        val v3 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        val v4 = Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        runTrx {      vectorD.crearVector(v3)
            vectorD.crearVector(v4)}
        Thread.sleep(1000)
        service.estadisticasDeUbicaciones(lista,"dengue")
        service.estadisticasDeUbicaciones(lista,"covid")
        var pointsResult = curva.obtenerMediciones("buenos aires","dengue")
        Assert.assertEquals(4,pointsResult[0].cantVectores)
        Assert.assertEquals(6,pointsResult[1].cantVectores)
        Assert.assertEquals(8,pointsResult[2].cantVectores)
        //Assert.assertEquals(100.0F,curva.obtenerPromedioDeContagiosDe("buenos aires","dengue"))
    }

    @After
    fun cleanUp(){
        dataService.eliminarTodo()
        ubicacionNeoDao.clear()
        //curva.borrar()
    }
}