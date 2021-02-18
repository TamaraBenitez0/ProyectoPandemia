package ar.edu.unq.eperdemic.modelo

import java.util.*

class EventoMutacion:Evento {


    var tipoPatogeno: String
    var idMutacion:Int ?= null

    constructor(descripcion:String,tipoPatogeno:String,idMutacion:Int,fecha: Date):super(fecha,descripcion) {
        this.idMutacion = idMutacion
        this.tipoPatogeno =tipoPatogeno // ej:bacteria,virus,hongo


    }

    constructor(descripcion:String,tipoPatogeno:String,fecha: Date):super(fecha,descripcion) {

        this.tipoPatogeno =tipoPatogeno // ej:bacteria,virus,hongo


    }

}