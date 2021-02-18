package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class RequerimientosMutacion() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    var puntosRequeridos: Int = 0

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, targetEntity = Mutacion::class)
    lateinit var mutacionesRequeridas: MutableSet<Mutacion>

    constructor(puntosRequeridos: Int, mutacionesRequeridas: MutableSet<Mutacion>) : this() {

        this.puntosRequeridos = puntosRequeridos
        this.mutacionesRequeridas=mutacionesRequeridas

    }

}