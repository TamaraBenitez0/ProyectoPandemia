package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.RequerimientosMutacion

interface RequerimientosMutacionDAO {
    fun guardar(requerimiento:RequerimientosMutacion)
}