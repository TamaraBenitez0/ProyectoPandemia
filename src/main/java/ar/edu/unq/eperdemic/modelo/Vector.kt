package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import javax.persistence.*
import kotlin.collections.HashSet


@Entity
class Vector() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null


    @ManyToMany(mappedBy = "vectores", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, targetEntity = Especie::class)
    var especies: MutableSet<Especie> = HashSet()

    lateinit var tipo: VectorFrontendDTO.TipoDeVector

    @Transient
    var probabilidadDeContagio = ProbabilidadDeContagio()

    @ManyToOne
    var lugar: Ubicacion? = null

    constructor(tipo: VectorFrontendDTO.TipoDeVector, nombreUbicacion: String) : this() {
        this.tipo = tipo
        this.setUbicacion(nombreUbicacion)
    }

    fun setId(n: Int) {
        id = n
    }

    fun lugarDeResidencia(): String {
        return lugar!!.nombreUbicacion
    }

    //simula el movimiento del vector,funciona en la persistencia ya que al recuperar
    //el vector queda en la lista , pero en memoria no lo agrega a la lista
    fun setUbicacion(ubi: String) {
        lugar = Ubicacion(ubi)

    }


    fun estaInfectado(): Boolean {
        return especies.isNotEmpty()
    }

    //usado para simular el contagio a vectores solo en memoria
    //metodos utilizados para el vector test
    fun contagiar(vectores: List<Vector>) {
        vectores.forEach { vector -> this.contagiarSiPuedeAlVector(vector) }
    }

    fun contagiarSiPuedeAlVector(vectorAContagiar: Vector): Boolean {
        var seContagio: Boolean = false
        if (puedeContagiar(vectorAContagiar) && this.id != vectorAContagiar.id) {
            this.especies.forEach { especie ->
                val ret: Int = probabilidadDeContagio.calcularFactorContagio(especie, this)
                seContagio = probabilidadDeContagio.getResultadoDeContagio(ret)
                if (seContagio) {
                    vectorAContagiar.agregarEspecie(especie)

                }
            }
        }
        return seContagio
    }


    fun puedeContagiarA(vectorAContagiar: Vector):Boolean{
        return puedeContagiar(vectorAContagiar) && this.id != vectorAContagiar.id
    }

    fun puedeEfectuarContagioDe(vectorAContagiar:Vector,especie: Especie):Boolean {
        val ret: Int = probabilidadDeContagio.calcularFactorContagio(especie, this)
        var resContagio = probabilidadDeContagio.getResultadoDeContagio(ret)
    return resContagio && !vectorAContagiar.especies.contains(especie)
    }

    fun contagiar(vectorAContagiar: Vector,especie:Especie){
        vectorAContagiar.agregarEspecie(especie)
    }


    fun puedeContagiar(vectorAContagiar: Vector): Boolean {
        return (vectorAContagiar.esPersona() || (vectorAContagiar.esAnimal() && this.esInsecto())
                || (vectorAContagiar.esInsecto() && (this.esPersona() || this.esAnimal())))
    }


    fun esPersona(): Boolean {
        return this.tipo == VectorFrontendDTO.TipoDeVector.Persona
    }

    fun esInsecto(): Boolean {
        return this.tipo == VectorFrontendDTO.TipoDeVector.Insecto
    }

    fun esAnimal(): Boolean {
        return this.tipo == VectorFrontendDTO.TipoDeVector.Animal
    }


    fun agregarEspecie(especie: Especie) {
        this.especies.add(especie)
        especie.vectores.add(this)
        especie.sumarCada5()
    }

    fun setProbabilidad(pro: ProbabilidadDeContagio) {
        this.probabilidadDeContagio = pro
    }


    fun caminoQuePuedeAtravesar(): String {
        var res = ""
        if (this.esAnimal()) {
            res = "Terrestre|Maritimo|Aereo"
        }
        if (this.esInsecto()) {
            res = "Terrestre|Aereo"
        }
        if(this.esPersona()){
          res="Terrestre|Maritimo"
       }
    return res
    }

    fun infecta(especie: Especie){
        this.agregarEspecie(especie)
    }
}
