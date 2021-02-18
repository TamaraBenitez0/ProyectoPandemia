package neo4j

import ar.edu.unq.eperdemic.modelo.Ubicacion

interface UbicacionNeoDao {
    fun crear(ubicacion:Ubicacion)
    fun conectarUbicacion(ubicacion1:String, ubicacion2:String, tipoCamino:String)
    fun existeUbicacion(nombreUbicacion:String):Boolean
    fun existeRelacion(nombreUbicacion1:String,nombreUbicacion2:String,tipoCamino:String):Boolean
    fun sePuedeLlegarAUbicacion(actual: String, tipo:String, ubicacionAMover:String):Boolean
    fun puedeLlegarEnUnMovimiento(actual: String, tipo:String, ubicacionAMover:String):Boolean
    fun conectados(nombreUbicacion: String):List<String>
    fun moverMasCorto(ubicacionActual:String, nombreDeUbicacion:String,unCamino:String):List<String>
    fun capacidadDeExpansion(ubicacion: String,unCamino: String, movimientos:Int): Int
    fun clear()
}