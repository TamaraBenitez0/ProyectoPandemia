package mongoDb

import ar.edu.unq.eperdemic.modelo.Evento
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Sorts.descending
import org.bson.conversions.Bson

class EventoMongoDao: GenericMongoDAO<Evento>(Evento::class.java),MongoEventoDAO {

   override fun obtenerEventosDe(vectorId:Int): List<Evento> {

        return findSort(or(eq("vectorId",vectorId),eq("contagiaA",vectorId)),descending("fecha"))


   }

    override fun obtenerEventosEn(nombreUbicacion:String):List<Evento> {
        return   findSort(eq("ubicacion",nombreUbicacion), descending("fecha"))
    }

    //utilizamos la descripcion para filtrar
    override fun getEventosDePatogeno(tipoPatogeno:String) : List<Evento>{
        var primeraAparicionDeEspecie =eq("descripcion","la especie llega por primera vez")
        var aparicionDePandemia= eq("descripcion","Es especie pandemia")
        var creacionDeEspeciePatogeno=eq("descripcion","Se ha creado una nueva especie")
        var aparicionMutacion =eq("descripcion","El patogeno adquirio una mutacion")
        var condiciones =and(eq("tipoPatogeno",tipoPatogeno),or(or(or(primeraAparicionDeEspecie,aparicionDePandemia),aparicionMutacion),creacionDeEspeciePatogeno))
        return findSort(condiciones ,descending("fecha"))
    }

    override fun esPandemiaRegistrada(especieId: Int): Boolean {
        return existe(and(eq("especieId",especieId),eq("descripcion","Es especie pandemia")))
    }

    override fun esPrimeraAparicionEn(especieId:Int,ubicacion:String):Boolean{
        return existe(and(eq("especieId",especieId),eq("ubicacion",ubicacion)))

    }

    fun existe(filter: Bson):Boolean{
        return find(filter).isNotEmpty()
    }

}