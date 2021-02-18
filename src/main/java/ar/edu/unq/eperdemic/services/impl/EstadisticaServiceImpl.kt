package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class EstadisticaServiceImpl(val especieDao:EspecieDAO,val ubicacionDao:UbicacionDAO):EstadisticasService {
    override fun especieLider(): Especie {
      return runTrx{especieDao.infectoAMasHumanos(VectorFrontendDTO.TipoDeVector.Persona)}
    }

    override fun lideres(): List<Especie> {
        return runTrx { especieDao.lideresEspecie(VectorFrontendDTO.TipoDeVector.Persona,VectorFrontendDTO.TipoDeVector.Animal) }
    }

    override fun reporteDeContagios(nombreUbicacion: String): ReporteDeContagios {
        val cantVecPre:Int= runTrx { ubicacionDao.cantidadDeVectoresPresentes(nombreUbicacion) }//
        val cantVecInf:Int= runTrx { ubicacionDao.cantidadDeVectoresInfectados(nombreUbicacion) }//
        val eMInfecciosa:String= runTrx {ubicacionDao.especieMasInfecciosa(nombreUbicacion)}//testeados en cada dao
        return ReporteDeContagios(cantVecPre,cantVecInf,eMInfecciosa)
    }
}