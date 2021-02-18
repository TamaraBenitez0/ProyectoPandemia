package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
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
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.impl.EstadisticaServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import exceptions.ErrorNotLider
import org.junit.After
import org.junit.Assert
import org.junit.Test

class EstadisticaServiceTest {
    val dataService: DataService = DataServiceHibernate()
    val especieDao: EspecieDAO =HibernateEspecieDAO()
    val ubicacionDao:UbicacionDAO=HibernateUbicacionDAO()
    val estadistica : EstadisticasService = EstadisticaServiceImpl(especieDao,ubicacionDao)
    val patogenoDao:PatogenoDAO=HibernatePatogenoDAO()
    val vectorDao:VectorDAO=HibernateVectorDAO()


    @Test
    fun laEspecieLiderEsElCovid(){
        dataService.crearSetDeDatosIniciales() //instancio aca el set para poder testear facilmente la excepcion
        Assert.assertEquals("covid",estadistica.especieLider().nombre)
    }

    @Test(expected = ErrorNotLider::class)
    fun noExisteNingunaEspeciePorLoTantoNoHayEspecieLider(){
        Assert.assertEquals("covid",estadistica.especieLider().nombre)
    }

    @Test(expected = ErrorNotLider::class)
    fun existeUnaEspecieInfecciosaPeroNoAfectaANadie(){
        var bacteria = Patogeno("Bacteria")
        runTrx{patogenoDao.crear(bacteria)}
        var dengue = Especie(bacteria, "dengue", "brasil")
        runTrx{especieDao.guardar(dengue)}
        var insectoEnf= Vector()
       runTrx { vectorDao.crearVector(insectoEnf) }

        Assert.assertEquals("covid",estadistica.especieLider().nombre)
    }

    @Test
    fun hay2EspeciesLideresAfectandoHumanosYAnimales() {
        dataService.crearSetDeDatosIniciales()
           Assert.assertEquals(2,estadistica.lideres().size)
    }

    @Test
    fun noHayEspeciesLideres() {
        Assert.assertEquals(0,estadistica.lideres().size)
    }

    @Test
    fun seElaboraUnReporteDeContagiosDeBuenosAires(){
        dataService.crearSetDeDatosIniciales()
        var reporte =estadistica.reporteDeContagios("buenos aires")
        Assert.assertEquals(3,reporte.vectoresPresentes)
        Assert.assertEquals(2,reporte.vectoresInfecatados)
        Assert.assertEquals("covid",reporte.nombreDeEspecieMasInfecciosa)


    }

    @After
    fun cleanUp(){
        dataService.eliminarTodo()
    }

}