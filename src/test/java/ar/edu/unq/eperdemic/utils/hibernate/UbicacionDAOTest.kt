package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import ar.edu.unq.eperdemic.utils.hibernate.DataServiceHibernate
import exceptions.ErrorRecuperar
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UbicacionDAOTest {

    val dataService:DataService = DataServiceHibernate()
    var dao: UbicacionDAO = HibernateUbicacionDAO()
    var especieDao : EspecieDAO=HibernateEspecieDAO()
    var patogenoDao : PatogenoDAO = HibernatePatogenoDAO()
    var vectorDao : VectorDAO = HibernateVectorDAO()

    @Before
    fun setUp() {
        dataService.crearSetDeDatosIniciales()
    }

    @Test
    fun seGuardaYRecuperaUnaUbicacion() {

        var bsAs: Ubicacion = Ubicacion("italia")
        runTrx { dao.guardar(bsAs) }
        var ubicacion = runTrx { dao.recuperar("italia") }
        Assert.assertEquals("italia", ubicacion.nombreUbicacion)
    }

    @Test
    fun seActualizaUnaUbicacion() {
        var vector:Vector=Vector()
        var vector2:Vector=Vector()
        var ubicacion=runTrx { dao.recuperar("buenos aires") }
        ubicacion.agregarVector(vector)
        ubicacion.agregarVector(vector2)
        runTrx { dao.actualizar(ubicacion) }
        var ubiRec=runTrx { dao.cantidadDeVectoresPresentes("buenos aires") }
        Assert.assertEquals(5,ubiRec)
    }

    @Test(expected = ErrorRecuperar::class)
    fun seBorraUnaUbicacion(){
        var ubicacion=Ubicacion("chile")
        runTrx { dao.borrar(ubicacion) }
        var rec= runTrx { dao.recuperar("chile") }
        Assert.assertEquals("chile",rec.nombreUbicacion) //lanza excepcion
    }

    @Test
    fun hay2VectoresPresentesEnLaUbicacion() {
        var bsAs: Ubicacion = Ubicacion("Colombia")
        var vector:Vector=Vector()
        var vector2:Vector=Vector()
        bsAs.agregarVector(vector)
        bsAs.agregarVector(vector2)
        runTrx { dao.guardar(bsAs) }
        var cantVectores=runTrx { dao.cantidadDeVectoresPresentes("Colombia") }
        Assert.assertEquals(2,cantVectores)
    }

    @Test
    fun hay1VectorInfectadoEnLaUbicacion() {
        var cantVectores=runTrx { dao.cantidadDeVectoresInfectados("tokio") }
        Assert.assertEquals(1,cantVectores)
    }

    @Test
    fun nombreDeLaInfeccionMasInfecciosaEsElCovid() {

        var nombreE=runTrx { dao.especieMasInfecciosa("buenos aires") }
        Assert.assertEquals("covid",nombreE)
    }

    @Test
    fun nombreDeLaInfeccionMasInfecciosaInexistente() {

        var nombreE=runTrx { dao.especieMasInfecciosa("moscu") }
        Assert.assertEquals("",nombreE)
    }

    @After
    fun cleanUp(){
        dataService.eliminarTodo()

    }
}

