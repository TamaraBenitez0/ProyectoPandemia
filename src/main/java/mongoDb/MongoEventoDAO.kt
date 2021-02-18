package mongoDb

import ar.edu.unq.eperdemic.modelo.Evento

interface MongoEventoDAO {
    fun save(evento: Evento)
    fun obtenerEventosDe(vectorId:Int): List<Evento>
    fun obtenerEventosEn(nombreUbicacion:String):List<Evento>
    fun getEventosDePatogeno(tipoPatogeno: String) : List<Evento>
    fun esPandemiaRegistrada (especieId: Int) : Boolean
    fun esPrimeraAparicionEn(especieId: Int,nombreUbicacion: String):Boolean
    fun deleteAll()
}