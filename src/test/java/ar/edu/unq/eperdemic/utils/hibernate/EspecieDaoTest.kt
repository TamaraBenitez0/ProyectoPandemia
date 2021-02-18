package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EspecieDaoTest {

    var daoE: EspecieDAO = HibernateEspecieDAO()
    var daoP: PatogenoDAO = HibernatePatogenoDAO()
    var daoV: VectorDAO=HibernateVectorDAO()
    val dataService: DataService =DataServiceHibernate()

    @Before
    fun setUp(){
    dataService.crearSetDeDatosIniciales()
    }


    @Test
    fun seGuardaYRecuperaUnaEspecie() {

        var patogeno: Patogeno = Patogeno ("Virus")
        runTrx {daoP.crear(patogeno)}
        var especie: Especie = Especie(patogeno,"Gripe","España")

        runTrx { daoE.guardar(especie) }
        var especieRecuperada = runTrx { daoE.recuperar(6) }
        Assert.assertEquals("Gripe", especieRecuperada.nombre)
    }

    @Test
    fun seActualizaUnaEspecie(){

        var patogeno: Patogeno = Patogeno ("Virus")
        runTrx {daoP.crear(patogeno)}
        var especie: Especie = Especie(patogeno,"Gripe","España")
        runTrx { daoE.guardar(especie) }
        especie.nombre="SARScov-2"
        runTrx{daoE.actualizar(especie)}
        var especieRecuperada = runTrx { daoE.recuperar(6) }
        Assert.assertEquals("SARScov-2", especieRecuperada.nombre)

    }

    @Test
    fun unaEspecieConUnVectorHumanoContagiadoEsLaEspecieLider(){

        var eLider = runTrx{daoE.infectoAMasHumanos(VectorFrontendDTO.TipoDeVector.Persona) }
        Assert.assertEquals("covid",eLider.nombre)

    }

    @Test
    fun hay2LideresDeEspecie(){

        var eLideres = runTrx{daoE.lideresEspecie(VectorFrontendDTO.TipoDeVector.Persona,VectorFrontendDTO.TipoDeVector.Animal) }
        Assert.assertEquals(2,eLideres.size)

    }

    @After
    fun cleanUp(){
        dataService.eliminarTodo()
    }
}