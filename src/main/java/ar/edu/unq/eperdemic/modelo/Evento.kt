package ar.edu.unq.eperdemic.modelo

import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.*

open class Evento {
    @BsonProperty("id")
    val id: Int? = null
    lateinit var fecha : Date
    lateinit var descripcion:String

    constructor(fecha:Date,descripcion:String) {
        this.fecha=fecha
        this.descripcion=descripcion

    }

    protected constructor(){}

}