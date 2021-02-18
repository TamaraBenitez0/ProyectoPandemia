package ar.edu.unq.eperdemic.modelo


import javax.persistence.*

@Entity
class Ubicacion() {

    @Id
    lateinit var nombreUbicacion: String

    @OneToMany(mappedBy = "lugar", cascade = [CascadeType.ALL], fetch = FetchType.EAGER,targetEntity = Vector::class)
    var vectores: MutableSet<Vector> = HashSet()

    constructor(nombre:String):this(){
        this.nombreUbicacion=nombre
    }

    fun agregarVector(vector:Vector){
        vectores.add(vector)
        vector.lugar=this
    }

}