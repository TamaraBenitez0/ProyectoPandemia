package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Evento
import ar.edu.unq.eperdemic.services.FeedService
import mongoDb.MongoEventoDAO

class FeedServiceImpl(val evento: MongoEventoDAO): FeedService   {


    override fun feedPatogeno(tipoDePatogeno: String):List<Evento>{
        return evento.getEventosDePatogeno(tipoDePatogeno)
    }


    override fun feedVector(vectorId:Long):List<Evento>{
        return evento.obtenerEventosDe(vectorId.toInt())
    }


    override fun feedUbicacion(nombreUbicacion:String):List<Evento>{
        return evento.obtenerEventosEn(nombreUbicacion)
    }


}