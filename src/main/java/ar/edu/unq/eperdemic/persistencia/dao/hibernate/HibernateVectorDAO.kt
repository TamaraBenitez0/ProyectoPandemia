package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import exceptions.ErrorRecuperar

class HibernateVectorDAO : HibernateDAO<Vector>(Vector::class.java),
        VectorDAO {

    override fun crearVector(vector: Vector): Vector {
        val session = TransactionRunner.currentSession
        session.save(vector)
        return vector
    }


    override fun borrarVector(vectorId: Int) { //hecho con query porque no recordamos si el borrar ya estaba escrito en la inteface y no podemos modificarla si estaba
        val session = TransactionRunner.currentSession
        val hql = "delete Vector v where v.id =:ide"
        val query = session.createQuery(hql)
        query.setParameter("ide",vectorId)
        query.executeUpdate()
    }


    override fun infeccionesDe(id: Int): List<Especie> {
        val session = TransactionRunner.currentSession
        val hql = "select e from Vector v  join v.especies e on v.id =:ide " +
                "group by e.id"

        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("ide", id)
        return query.resultList

    }

    override fun vectoresInfectados():List<Vector> {
        val session = TransactionRunner.currentSession
        val hql = "select v " +
                "from Vector v  " +
                "join v.especies e " +
                "group by v.id " +
                "having count(e)>0"

        val query = session.createQuery(hql, Vector::class.java)
        return query.resultList
    }

}
