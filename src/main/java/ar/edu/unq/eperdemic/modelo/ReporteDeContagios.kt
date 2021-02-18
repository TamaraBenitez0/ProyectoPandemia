package ar.edu.unq.eperdemic.modelo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


class ReporteDeContagios() {


    var id:Int?=null

    var vectoresPresentes:Int=0

    var vectoresInfecatados:Int=0

    var nombreDeEspecieMasInfecciosa: String=""

    constructor(cantVectores:Int, cantInfectados:Int,nombreEspecie:String) :this()
    {
        this.vectoresPresentes = cantVectores
        this.vectoresInfecatados = cantInfectados
        this.nombreDeEspecieMasInfecciosa = nombreEspecie
    }
}