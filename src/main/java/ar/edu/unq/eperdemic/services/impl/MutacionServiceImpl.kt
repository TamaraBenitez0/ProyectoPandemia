package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.EventoMutacion
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import exceptions.ErrorMutar
import mongoDb.MongoEventoDAO
import java.util.*

class MutacionServiceImpl(val mutacionDao: MutacionDAO, val especieDao: EspecieDAO,val patogenoDao:PatogenoDAO, val daoMongo: MongoEventoDAO): MutacionService {
    override fun mutar(especieId: Int, mutacionId: Int) {

        runTrx {
            var especieRecuperada: Especie =  especieDao.recuperar(especieId)
            var mutacionRecuperada: Mutacion =  mutacionDao.recuperar(mutacionId)
            if(especieRecuperada.cumpleConAdn(mutacionRecuperada) && especieRecuperada.cumpleMutaciones(mutacionRecuperada)){
            especieRecuperada.mutar(mutacionRecuperada)
             patogenoDao.actualizar(especieRecuperada.patogeno!!)
             especieDao.actualizar(especieRecuperada)
                daoMongo.save(EventoMutacion("El patogeno adquirio una mutacion",especieRecuperada.patogeno!!.tipo,mutacionRecuperada.id!!, Date()))
            }
                else{

                throw ErrorMutar()
            }
        }
    }



    override fun crearMutacion(mutacion: Mutacion): Mutacion {
       return runTrx{mutacionDao.crearMutacion(mutacion)}
    }

    override fun recuperarMutacion(mutacionId: Int): Mutacion {
      return  runTrx { mutacionDao.recuperar(mutacionId) }
    }


}