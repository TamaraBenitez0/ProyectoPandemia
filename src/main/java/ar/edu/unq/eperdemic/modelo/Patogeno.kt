package ar.edu.unq.eperdemic.modelo

import java.io.Serializable
import javax.persistence.*

@Entity
class Patogeno() : Serializable{

    @OneToOne(fetch= FetchType.EAGER)
    var atributo:AtributoPatogeno? =null

    lateinit var tipo: String
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int? = null

    @OneToMany(mappedBy = "patogeno", cascade = [CascadeType.ALL], fetch = FetchType.EAGER,targetEntity = Especie::class)
    var especies:MutableSet<Especie> = HashSet()

    constructor(tipoPatogeno:String):this(){
        this.tipo=tipoPatogeno
    }

    override fun toString(): String {
        return tipo
    }

    fun setID(ide:Int){
        this.id=ide
    }

    fun getDefensa():Int{
        return atributo!!.defensa
    }
    fun getLetalidad():Int{
        return atributo!!.letalidad
    }
    fun getCapContagioHumano():Int{
        return atributo!!.capacidadContagioPersona
    }
    fun getCapContagioInsecto():Int{
        return atributo!!.capacidadContagioInsecto
    }
    fun getCapContagioAnimal():Int{
        return atributo!!.capacidadContagioAnimal
    }


    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String) : Especie{

        return Especie(this, nombreEspecie, paisDeOrigen)
    }

    fun setAts(ar:AtributoPatogeno){
        atributo=ar
    }

}
