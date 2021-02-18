package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.AtributoPatogeno

interface AtributoPatogenoDAO {
    fun guardar(ap:AtributoPatogeno)
}