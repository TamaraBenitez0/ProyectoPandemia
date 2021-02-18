package ar.edu.unq.eperdemic.modelo

import java.util.*

class EventoContagio:Evento{

    var tipoPatogeno:String
    var especieId: Int
    var ubicacion:String? = null
    var vectorId:Int?=null
    var contagiaA:Int? =null

    constructor(descripcion: String,vectorId:Int,tipoPatogeno:String,especieId:Int,contagiaA:Int,nombreU:String,fecha:Date):super(fecha,descripcion){
        this.vectorId=vectorId  //vector contagiado
        this.tipoPatogeno=tipoPatogeno //tipo de patogeno que adquiere
        this.especieId=especieId      // identificacion de la especie
        this.contagiaA=contagiaA        // vector al cual se contagia
        this.ubicacion=nombreU             //nombre de la ubicacion donde se produce el contagio

    }

    //utilizado para cuando ocurre solo una infeccion y no contagia a otro
    constructor(descripcion:String,contagiaA:Int,tipoPatogeno:String,especieId:Int,nombreU:String,fecha:Date):super(fecha,descripcion){
        this.contagiaA=contagiaA          //vector contagiado
        this.tipoPatogeno=tipoPatogeno //tipo de patogeno que adquiere
        this.especieId=especieId      // identificacion de la especie
        this.ubicacion=nombreU             //nombre de la ubicacion donde se produce el contagio

    }

    constructor(descripcion: String, especieId: Int,tipoPatogeno:String, fecha:Date):super(fecha,descripcion){
        this.especieId = especieId   // identificacion de la especie
        this.tipoPatogeno = tipoPatogeno    //tipo de patogeno que adquiere
    }
}
