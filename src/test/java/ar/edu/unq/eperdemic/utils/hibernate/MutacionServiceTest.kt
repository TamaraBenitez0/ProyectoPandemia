package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.RequerimientosMutacion
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateMutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateRequerimientosMutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.RequerimientosMutacionDAO
import ar.edu.unq.eperdemic.services.impl.MutacionServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import exceptions.ErrorMutar
import mongoDb.EventoMongoDao
import mongoDb.MongoEventoDAO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MutacionServiceTest {

    var daoE: EspecieDAO = HibernateEspecieDAO()
    val mongo: MongoEventoDAO = EventoMongoDao()
    var dataService: DataService = DataServiceHibernate()
    var dao : MutacionDAO = HibernateMutacionDAO()
    var daoP: PatogenoDAO =HibernatePatogenoDAO()
    var mutacionService = MutacionServiceImpl(dao,daoE,daoP,mongo)
    var especieDao:EspecieDAO=HibernateEspecieDAO()
    var daoR : RequerimientosMutacionDAO = HibernateRequerimientosMutacionDAO()

    @Before
    fun setUp() {
        dataService.crearSetDeDatosIniciales()
    }

    @Test
    fun seCreaYSeRecuperaUnaMutacion() {
        var requerimientos  = RequerimientosMutacion(2, hashSetOf())
        runTrx { daoR.guardar(requerimientos) }
        var mutacion  = Mutacion (4, "Letalidad",requerimientos )
        mutacionService.crearMutacion(mutacion)
        var recuperar=mutacionService.recuperarMutacion(2)
        Assert.assertEquals(4,recuperar.puntosAtributo)
    }

    @Test
    fun unaEspeciePuedeMutar() {


        mutacionService.mutar(3,1)
        var recuperarMutacion =  mutacionService.recuperarMutacion(1)
        Assert.assertEquals("rabia",recuperarMutacion.especie!!.nombre) //
        Assert.assertEquals(2,recuperarMutacion.especie!!.adn) // en ambas supongo que ya la mutacion tiene la especie
       Assert.assertEquals(4,recuperarMutacion.especie!!.patogeno!!.atributo!!.letalidad)//testea que adquiera letalidad en 4
        mutacionService.mutar(3,2)
        var recuperarMutacion2 =  mutacionService.recuperarMutacion(2)
        Assert.assertEquals(4,recuperarMutacion2.especie!!.patogeno!!.atributo!!.defensa)

    }

    @Test(expected = ErrorMutar::class)
    fun unaEspecieQueNoCumpleConLasMutacionesPreviasNoPuedeMutar() {

        mutacionService.mutar(3,2)


    }


    @Test(expected = ErrorMutar::class)
    fun unaEspecieNoPuedeMutarPorQueNoTieneSuficienteAdn() {

        mutacionService.mutar(1,1)

    }

    @Test
    fun unaEspecieCumpleLosRequerimientosParaMutar(){
        var especie= runTrx { especieDao.recuperar(3) }
        var mutacion=mutacionService.recuperarMutacion(1)
        Assert.assertTrue(especie.cumpleMutaciones(mutacion))
    }

    @Test
    fun unaEspecieNoCumpleLosRequerimientosParaMutar(){
        var especie= runTrx { especieDao.recuperar(3) }
        var mutacion=mutacionService.recuperarMutacion(2)
        Assert.assertFalse(especie.cumpleMutaciones(mutacion))
    }

    @After
    fun cleanUp(){

        dataService.eliminarTodo()

    }



}