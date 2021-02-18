package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Ubicacion

interface MutacionDAO {

    fun crearMutacion(mutacion: Mutacion): Mutacion
    fun recuperar(id:Int): Mutacion
    fun actualizar(mutacion: Mutacion)
}