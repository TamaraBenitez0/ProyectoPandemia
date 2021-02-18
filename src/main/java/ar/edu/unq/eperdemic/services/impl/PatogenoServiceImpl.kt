package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.EventoContagio
import ar.edu.unq.eperdemic.modelo.EventoMutacion
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import mongoDb.MongoEventoDAO
import java.util.*

class PatogenoServiceImpl(val patogenoDAO: PatogenoDAO, val especieDao:EspecieDAO,val ubicacionDao: UbicacionDAO, val daoMongo: MongoEventoDAO) : PatogenoService {

    override fun crearPatogeno(patogeno: Patogeno): Int {
       return runTrx{patogenoDAO.crear(patogeno)}
    }//

    override fun recuperarPatogeno(id: Int): Patogeno {
        return runTrx {patogenoDAO.recuperar(id)}
    }//

    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return  runTrx {patogenoDAO.recuperarATodos()}
    }//

    override fun agregarEspecie(id: Int, nombreEspecie: String, paisDeOrigen: String): Especie {
        return runTrx{
            var especie =patogenoDAO.recuperar(id).crearEspecie(nombreEspecie,paisDeOrigen)
            especieDao.guardar(especie)
            patogenoDAO.actualizar(especie.patogeno!!)
            daoMongo.save(EventoMutacion("Se ha creado una nueva especie",especie.patogeno!!.tipo, Date()))
        especie
        }

    }//

    override fun cantidadDeInfectados(especieId: Int): Int {
        return runTrx { patogenoDAO.cantidadDeVectoresInfectados(especieId) }
    }

    override fun esPandemia(especieId: Int): Boolean {
        return runTrx { ubicacionDao.esEspeciePandemia(especieId) }
    }

    override fun recuperarEspecie(id: Int): Especie {
        return runTrx {especieDao.recuperar(id)}
    }
}