package ar.edu.unq.eperdemic.modelo

import java.util.*

class EventoArribo: Evento {

    var ubicacion:String
    var vectorId:Int


    constructor(descripcion:String,vectorId:Int,ubicacionArribada:String,fecha: Date):super(fecha,descripcion){
        this.vectorId=vectorId
        this.ubicacion=ubicacionArribada

    }
}