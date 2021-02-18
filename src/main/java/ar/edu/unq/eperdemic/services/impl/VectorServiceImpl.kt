package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.EventoContagio
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import exceptions.ErrorActualizar
import exceptions.ErrorContagio
import mongoDb.MongoEventoDAO
import java.util.*

class VectorServiceImpl(val vectorDAO: VectorDAO,val daoMongo:MongoEventoDAO,val ubicacionDAO:UbicacionDAO) : VectorService {

    override fun crearVector(vector: Vector): Vector {
        return runTrx{vectorDAO.crearVector(vector)}
    }

    override fun recuperarVector(vectorId: Int): Vector {
        return runTrx { vectorDAO.recuperar(vectorId) }
    }

    override fun borrarVector(vectorId:Int) {
        runTrx{vectorDAO.borrarVector(vectorId)}
    }

    override fun actualizarVector(vector: Vector) {
           if(vector.id==null){
               throw ErrorActualizar()
           }
            runTrx { vectorDAO.actualizar(vector) }
        }

    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        var cant:Int= 0
        if (!vectorInfectado.estaInfectado()) {
            throw ErrorContagio()
        }
        runTrx {
            vectores.forEach { vector ->
                if (vectorInfectado.puedeContagiarA(vector)) {
                    vectorInfectado.especies.forEach { especie ->
                        this.intentoDeContagio(especie, vector, vectorInfectado)
                    }
                }
            }
        }
    }

    fun intentoDeContagio(especie: Especie,vector: Vector,vectorInfectado: Vector){
        if (vectorInfectado.puedeEfectuarContagioDe(vector,especie)) {
            vectorInfectado.contagiar(vector, especie)
            vectorDAO.actualizar(vector)
            this.eventoMongoContagio(especie,vector,vectorInfectado)
        }
    }

    fun eventoMongoContagio(especie: Especie,vector: Vector,vectorInfectado: Vector){
        if (!daoMongo.esPrimeraAparicionEn(especie.id!!,vector.lugarDeResidencia())){
            var msg= "la especie llega por primera vez"
            daoMongo.save(EventoContagio(msg,vectorInfectado.id!!,especie.patogeno!!.tipo,especie.id!!,vector.id!!,vector.lugarDeResidencia(), Date()))
            verificarSiRegitrarPandemia(especie)
        }
        else{
            var msg= "el vector ${vectorInfectado.id} contagia a otro vector con ${especie.nombre}"
            daoMongo.save(EventoContagio(msg,vectorInfectado.id!!,especie.patogeno!!.tipo,especie.id!!,vector.id!!,vector.lugarDeResidencia(), Date()))
        }
    }

    fun verificarSiRegitrarPandemia(especie:Especie){
    if (ubicacionDAO.esEspeciePandemia(especie.id!!) && !daoMongo.esPandemiaRegistrada(especie.id!!)) {
        var msg = "Es especie pandemia"
        daoMongo.save(EventoContagio(msg, especie.id!!, especie.patogeno!!.tipo, Date()))
        }
    }



    override fun infectar(vector: Vector, especie: Especie) {
       runTrx{ vector.infecta(especie)
             vectorDAO.actualizar(vector)
            if (daoMongo.esPrimeraAparicionEn(especie.id!!,vector.lugarDeResidencia())){
                var msg="la especie llega por primera vez"
                daoMongo.save(EventoContagio(msg,vector.id!!,especie.patogeno!!.tipo,especie.id!!,vector.lugarDeResidencia(), Date()))
                verificarSiRegitrarPandemia(especie)

            }

            else{
                var msg= "la especie ${especie.nombre} infecto al vector "
                daoMongo.save(EventoContagio(msg,vector.id!!,especie.patogeno!!.tipo,especie.id!!,vector.lugarDeResidencia(),Date()))
            }
       }
    }


    override fun enfermedades(vectorId: Int): List<Especie> {
        return runTrx { vectorDAO.infeccionesDe(vectorId) }
    }
}

