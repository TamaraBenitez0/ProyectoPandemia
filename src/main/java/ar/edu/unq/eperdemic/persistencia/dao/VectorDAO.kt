package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorDAO {
    fun crearVector(vector:Vector):Vector
    fun recuperar(vectorId: Int): Vector
    fun borrarVector(vectorId: Int)
    fun actualizar(vector: Vector)
    fun infeccionesDe(id:Int): List<Especie>
    fun vectoresInfectados():List<Vector>
}