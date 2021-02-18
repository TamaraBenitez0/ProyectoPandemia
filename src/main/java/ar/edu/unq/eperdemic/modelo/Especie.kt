package ar.edu.unq.eperdemic.modelo

import exceptions.ErrorMutar
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
class Especie() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
    var nombre: String? = null
    var adn: Int = 0


    @ManyToOne
    var patogeno: Patogeno? = null

    var paisDeOrigen: String? = null


    @ManyToMany(fetch = FetchType.EAGER)
    var vectores: MutableSet<Vector> = HashSet()

    @OneToMany(mappedBy = "especie", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, targetEntity = Mutacion::class)
    var mutaciones: MutableSet<Mutacion> = HashSet()


    constructor(patogeno: Patogeno, nombre: String, paisDeOrigen: String) : this() {
        this.patogeno = patogeno
        this.nombre = nombre
        this.paisDeOrigen = paisDeOrigen

    }

    fun getFactorContagioSegunTipo(vector: Vector): Int {
        var capacidad = 0
        if (vector.esPersona()) {
            capacidad = patogeno!!.getCapContagioHumano()
        }
        if (vector.esAnimal()) {
            capacidad = patogeno!!.getCapContagioAnimal()
        }
        if (vector.esInsecto()) {
            capacidad = patogeno!!.getCapContagioInsecto()
        }
        return capacidad
    }


    fun set(nombre: String) {
        this.nombre = nombre
    }


    fun sumarCada5() {
        if (vectores.size % 5 == 0) {
            this.adn++
        }
    }

    fun restarAdn(puntosRequeridos: Int) {

        this.adn = adn - puntosRequeridos
    }

    fun agregarMutacion(mutacionRecuperada: Mutacion) {

        this.mutaciones.add(mutacionRecuperada)
        mutacionRecuperada.especie = this
    }


    fun mutar(mutacion: Mutacion) {


        this.restarAdn(mutacion.requerimientos.puntosRequeridos)
        patogeno!!.atributo!!.actualizarValor(mutacion,mutacion.puntosAtributo)
        this.agregarMutacion(mutacion)

    }

    fun cumpleConAdn(mutacion:Mutacion) : Boolean{

        return this.adn >= mutacion.requerimientos.puntosRequeridos
    }

    fun cumpleMutaciones(mutacion:Mutacion) : Boolean {
        var mutacionesNecesarias= mutacion.requerimientos.mutacionesRequeridas
        return this.mutaciones.containsAll(mutacionesNecesarias)
    }
}
