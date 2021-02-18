package ar.edu.unq.eperdemic.services.impl

import InfluxDB.GenericCurvaDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.EventoArribo
import ar.edu.unq.eperdemic.modelo.EventoContagio
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import exceptions.ErrorActualizar
import exceptions.UbicacionMuyLejana
import exceptions.UbicacionNoAlcanzable
import mongoDb.MongoEventoDAO
import neo4j.UbicacionNeoDao
import org.influxdb.dto.Point
import java.util.*


class UbicacionServiceImpl(val vectorDao:VectorDAO ,val dao:UbicacionDAO,val daoNeo:UbicacionNeoDao,val daoMongo: MongoEventoDAO,val curva:GenericCurvaDAO) : UbicacionService {

    override fun mover(vectorId: Int, nombreUbicacion: String) {
        runTrx { this.moverLindante(vectorId,nombreUbicacion) }
    }


    override fun expandir(nombreUbicacion: String) {
        runTrx {
            var vectores = dao.recuperarTodosVectores(nombreUbicacion)
            var vectoresInf = vectorDao.vectoresInfectados()

            if (vectoresInf.isNotEmpty()) {
                var infectadoRandom = vectoresInf.random()
                vectores.forEach { vector ->
                    if (infectadoRandom.puedeContagiarA(vector)) {
                        infectadoRandom.especies.forEach { especie ->
                            if (infectadoRandom.puedeEfectuarContagioDe(vector,especie)) {
                                infectadoRandom.contagiar(vector, especie)
                                vectorDao.actualizar(vector)
                               generarEventos(especie,infectadoRandom,vector)
                            }
                        }
                    }
                }
            }
        }
    }

    fun verificarSiRegistrarPandemia(especie: Especie){
        if (dao.esEspeciePandemia(especie.id!!) && !daoMongo.esPandemiaRegistrada(especie.id!!)) {
            var msg = "Es especie pandemia"
            daoMongo.save(EventoContagio(msg, especie.id!!, especie.patogeno!!.tipo, Date()))
        }
    }



    override fun crearUbicacion(nombreUbicacion: String): Ubicacion {
        var ubicacion = Ubicacion(nombreUbicacion)
        runTrx {
            dao.guardar(ubicacion)
        }
        daoNeo.crear(ubicacion)
        return ubicacion
    }

    override fun recuperarUbicacion(nombreUbicacion: String): Ubicacion {

        return runTrx {

            dao.recuperar(nombreUbicacion)
        }
    }

    override fun actualizarUbicacion(ubi: Ubicacion) {
        if (ubi.nombreUbicacion == null) {
            throw ErrorActualizar()
        }
        runTrx {

            dao.actualizar(ubi)
        }
    }

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        daoNeo.conectarUbicacion(ubicacion1, ubicacion2, tipoCamino)
    }

    override fun conectados(nombre: String): List<String> {
        return daoNeo.conectados(nombre)
    }

    //verifica si se puede llegar a la ubicacion a mover
    fun verificarSiPuedeAlcanzarUbicacion(actual: String, tipo: String, ubiAMover: String) {
        if (!daoNeo.sePuedeLlegarAUbicacion(actual, tipo, ubiAMover)) {
            throw UbicacionNoAlcanzable()
        }

    }

    //verifica si el vector puede alcanzar la ubicacion en 1 movimiento
    fun verificarSiEsLejana(actual: String, tipoCaminoVector: String, aMover: String) {
        if (!daoNeo.puedeLlegarEnUnMovimiento(actual, tipoCaminoVector, aMover)) {
            throw UbicacionMuyLejana()
        }
    }

    override fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String) {
        runTrx {
            val vectorViajero = vectorDao.recuperar(vectorId.toInt())
            val caminoVector = vectorViajero.caminoQuePuedeAtravesar()
            val ubicacionActual = vectorViajero.lugarDeResidencia()
            val ubicaciones = daoNeo.moverMasCorto(ubicacionActual, nombreDeUbicacion, caminoVector)
            if (ubicaciones.isNotEmpty()) {
                ubicaciones.forEach { u ->
                    this.moverLindante(vectorId.toInt(), u)
                }
            } else {
                throw UbicacionNoAlcanzable()
            }
        }
    }

    fun moverLindante(vectorId: Int, nombreUbicacion: String) {

        var vectorViajero = vectorDao.recuperar(vectorId)
        var lugar = vectorViajero.lugarDeResidencia()
        if (lugar != nombreUbicacion) {
            verificarSiPuedeAlcanzarUbicacion(lugar, vectorViajero.caminoQuePuedeAtravesar(), nombreUbicacion)
            verificarSiEsLejana(lugar, vectorViajero.caminoQuePuedeAtravesar(), nombreUbicacion)
            vectorViajero.setUbicacion(nombreUbicacion)
            var msg = "el vector arribo a la ubicacion"
            daoMongo.save(EventoArribo(msg, vectorId, nombreUbicacion, Date()))

            var vectores = dao.recuperarTodosVectores(nombreUbicacion)
            vectores.forEach { vector ->
                if (vectorViajero.puedeContagiarA(vector)) {
                    vectorViajero.especies.forEach { especie ->
                        if (vectorViajero.puedeEfectuarContagioDe(vector,especie)) {
                            vectorViajero.contagiar(vector, especie)
                            vectorDao.actualizar(vector)
                            generarEventos(especie,vectorViajero,vector)
                            }
                        }
                    }
                }
            }
        }

    fun generarEventos(especie:Especie,vectorInf:Vector,vector:Vector) {
        if (!daoMongo.esPrimeraAparicionEn(especie.id!!, vector.lugarDeResidencia())) {
            var msg = "la especie llega por primera vez"
            daoMongo.save(EventoContagio(msg,vectorInf.id!!,especie.patogeno!!.tipo,especie.id!!,vector.id!!,vector.lugarDeResidencia(), Date()))
            verificarSiRegistrarPandemia(especie)
        } else {
            var msg = "el vector ${vectorInf.id} contagia a otro vector con ${especie.nombre}"
            daoMongo.save(EventoContagio(msg, vectorInf.id!!, especie.patogeno!!.tipo, especie.id!!, vector.id!!, vector.lugarDeResidencia(), Date()))

        }
    }

override fun capacidadDeExpansion(vectorId: Long, movimientos:Int): Int{
        return runTrx {
            val vectorViajero = vectorDao.recuperar(vectorId.toInt())
            val caminoVector = vectorViajero.caminoQuePuedeAtravesar()
            val ubicacionActual = vectorViajero.lugarDeResidencia()
            val cantidad = daoNeo.capacidadDeExpansion(ubicacionActual, caminoVector, movimientos)
            cantidad
        }
    }

// se le puede pasar una especie y asi ver el tipo patogeno
override  fun generarEstadisticasDeEspecie(nombreUbicacion: String,nombreE:String){
    runTrx { var cant = dao.cantDeEspecieEnU(nombreUbicacion,nombreE)
        var infectados= dao.cantidadDeVectoresInfectados(nombreUbicacion)
        var total =dao.cantidadDeVectoresPresentes(nombreUbicacion)
        var calculo=0.0
        if (total!=0) {
            calculo = (cant * 100.0).div(total)
        }
        val point1: Point = Point.measurement("ubicaciones")
                .tag("nombreUbicacion",nombreUbicacion)
                .addField("contagios", cant)
                .addField("TipoEspecie",nombreE)
                .addField("PromedioDeInfectados", calculo)
                .addField("cantidadDeVectores",total)
                .build()
        curva.write(point1)
    }
}

    //se abren muchas sesiones para el run trx
    //se arregla con subfuncion
    fun estadisticaDeEspecies(nombreUbicacion: String,especies:List<String>){
        runTrx{ especies.forEach { especie-> generarEstadisticasDeEspecie(nombreUbicacion,especie) }}
    }

    override fun estadisticasDeUbicaciones(nombreUbis:List<String>,nombreE:String){
        nombreUbis.forEach { nombreU-> generarEstadisticasDeEspecie(nombreU,nombreE)}
    }




}
