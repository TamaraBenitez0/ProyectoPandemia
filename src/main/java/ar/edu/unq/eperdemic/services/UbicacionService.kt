package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Ubicacion

interface UbicacionService {

    fun mover(vectorId: Int, nombreUbicacion: String)
    fun expandir(nombreUbicacion: String)
    /* Operaciones CRUD*/
    fun crearUbicacion(nombreUbicacion: String): Ubicacion
    fun recuperarUbicacion(nombreUbicacion:String) : Ubicacion
    fun actualizarUbicacion(ubi:Ubicacion)
    fun conectar(ubicacion1:String, ubicacion2:String, tipoCamino:String)
    fun conectados(nombre:String):List<String>
    fun moverMasCorto(vectorId:Long, nombreDeUbicacion:String)
    fun capacidadDeExpansion(vectorId: Long, movimientos:Int): Int
    fun generarEstadisticasDeEspecie(nombreUbicacion: String,nombreE:String)
    fun estadisticasDeUbicaciones(nombreUbis:List<String>,nombreE:String)
}