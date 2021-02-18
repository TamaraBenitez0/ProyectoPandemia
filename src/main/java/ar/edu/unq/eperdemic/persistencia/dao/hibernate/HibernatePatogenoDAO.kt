package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import exceptions.ErrorRecuperar

open class HibernatePatogenoDAO : HibernateDAO<Patogeno>(Patogeno::class.java),
        PatogenoDAO {


    override fun crear(patogeno : Patogeno) : Int {
        val session = TransactionRunner.currentSession
        session.save(patogeno)
        return patogeno.id!!
    }

    override fun recuperarATodos(): List<Patogeno> {
        val session = TransactionRunner.currentSession
        val hql = "from Patogeno i " + "order by i.id asc"

        val query = session.createQuery(hql, Patogeno::class.java)
        return query.resultList
    }


    override fun cantidadDeVectoresInfectados(id: Int): Int {
        val session = TransactionRunner.currentSession
        val hql = "select count(v) " +
                "from Especie e " +
                "join e.vectores v where e.id =:ide "


        val query = session.createQuery(hql)
        query.setParameter("ide", id)
        var valor = query.singleResult as Long
        return valor.toInt()
    }

}
