package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import exceptions.ErrorNotLider
import exceptions.ErrorRecuperar

class HibernateEspecieDAO:HibernateDAO<Especie>(Especie::class.java), EspecieDAO {

    override fun infectoAMasHumanos(tipo: VectorFrontendDTO.TipoDeVector): Especie {
        val session = TransactionRunner.currentSession
        val hql = "select e " +
                "from Vector v  " +
                "join v.especies e where v.tipo=:type " +
                "group by e.id " +
                "order by count(e.id) desc"

        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("type", tipo)
        query.maxResults = 1
        if (query.resultList.size == 0) {
            throw ErrorNotLider()
        }
        return query.resultList.get(0)
    }

    override fun lideresEspecie(pers: VectorFrontendDTO.TipoDeVector, animal: VectorFrontendDTO.TipoDeVector): List<Especie> {
        val session = TransactionRunner.currentSession
        val hql = "select e " +
                "from Vector v  " +
                "join v.especies e where v.tipo=:typeH or v.tipo=:typeA " +
                "group by e.id " +
                "order by count(e.id) desc"

        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("typeH", pers)
        query.setParameter("typeA", animal)
        query.maxResults = 10
        return query.resultList
    }

}