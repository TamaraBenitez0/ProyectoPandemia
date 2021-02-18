package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.RequerimientosMutacion
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateMutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateRequerimientosMutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.RequerimientosMutacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import exceptions.ErrorRecuperar
import org.junit.After
import org.junit.Assert
import org.junit.Test

class MutacionDAOTest {

    var dao : MutacionDAO = HibernateMutacionDAO()
    var dataService: DataService = DataServiceHibernate()
    var daoR : RequerimientosMutacionDAO = HibernateRequerimientosMutacionDAO()

    @Test
    fun seCreaYSeRecuperaUnaMutacion() {

        var requerimientos  = RequerimientosMutacion(2, hashSetOf())
        runTrx {daoR.guardar(requerimientos)}
        var mutacion  = Mutacion (4, "Letalidad",requerimientos )
        runTrx {dao.crearMutacion(mutacion)}
        var recuperarMutacion = runTrx { dao.recuperar(1)}
        Assert.assertEquals(4,recuperarMutacion.puntosAtributo)
    }

    @Test
    fun seActualizaUnaMutacion() {

        var requerimientos  = RequerimientosMutacion(2, hashSetOf())
        runTrx { daoR.guardar(requerimientos)}
        var mutacion  = Mutacion (4, "Letalidad",requerimientos )
        runTrx {dao.crearMutacion(mutacion)}
        mutacion.puntosAtributo=9
        runTrx { dao.actualizar(mutacion) }
        var recuperarMutacion = runTrx { dao.recuperar(1)}
        Assert.assertEquals(9,recuperarMutacion.puntosAtributo)
    }

    @Test (expected = ErrorRecuperar::class)
    fun noSePuedeRecuperarUnaMutacion() {
        runTrx {dao.recuperar(5)}


    }


    @After
    fun cleanUp(){
        dataService.eliminarTodo()

    }
}