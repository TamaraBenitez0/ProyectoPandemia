package ar.edu.unq.eperdemic.modelo

import java.awt.geom.PathIterator
import java.io.Serializable
import javax.persistence.*

@Entity
class AtributoPatogeno : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int? = null

    @ElementCollection
    var atributosExistentes: MutableMap<String, Int> = HashMap<String, Int>()

    var capacidadContagioPersona: Int = 0
    var capacidadContagioAnimal: Int = 0
    var capacidadContagioInsecto: Int = 0
    var letalidad:Int=0
    var defensa:Int=0

    fun valoresIniciales() {
        atributosExistentes.put("Letalidad", 0)
        atributosExistentes.put("Defensa", 0)
        atributosExistentes.put("ContagioAnimal", 0)
        atributosExistentes.put("ContagioHumano", 0)
        atributosExistentes.put("ContagioInsecto", 0)
    }

    constructor () {
        this.valoresIniciales()
    }

    fun actualizarValor(mutacion: Mutacion, puntosAtributo: Int) {
        var valor = atributosExistentes.get(mutacion.atributoAumentar)!! + (puntosAtributo)
        atributosExistentes.put(mutacion.atributoAumentar, valor)
        this.actualizarAtributos(atributosExistentes)
    }

    fun actualizarAtributos(dicc:MutableMap<String,Int>){
        this.letalidad=dicc.get("Letalidad")!!
        this.defensa=dicc.get("Defensa")!!
        this.capacidadContagioPersona=dicc.get("ContagioHumano")!!
        this.capacidadContagioAnimal=dicc.get("ContagioAnimal")!!
        this.capacidadContagioInsecto=dicc.get("ContagioInsecto")!!

    }
}
