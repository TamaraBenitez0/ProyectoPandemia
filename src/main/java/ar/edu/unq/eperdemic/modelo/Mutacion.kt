package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Mutacion() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Int?= null

    @ManyToOne
    var especie:Especie?= null

    lateinit var atributoAumentar:String

    var puntosAtributo:Int=0

    @ManyToOne
    lateinit var requerimientos:RequerimientosMutacion


    constructor (puntosAtributo:Int,aumentarA:String,requerimientos:RequerimientosMutacion) : this() {

        this.requerimientos=requerimientos
        this.puntosAtributo=puntosAtributo
        this.atributoAumentar=aumentarA

    }



}