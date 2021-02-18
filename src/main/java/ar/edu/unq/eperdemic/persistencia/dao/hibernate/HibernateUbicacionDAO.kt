package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import exceptions.ErrorRecuperar
import java.lang.RuntimeException

open class HibernateUbicacionDAO:HibernateDAO<Ubicacion>(Ubicacion::class.java),UbicacionDAO {
    override fun recuperar(nombre: String): Ubicacion {
        if(!existe(nombre)){
            throw ErrorRecuperar()
        }

        val session = TransactionRunner.currentSession
        val hql = ("from Ubicacion i where i.nombreUbicacion =:ubicacion ")
        val query = session.createQuery(hql, Ubicacion::class.java)
        query.setParameter("ubicacion", nombre)
        query.maxResults = 1
        return query.singleResult
    }

    fun existe(nombre: String): Boolean {
        val session = TransactionRunner.currentSession
        val hql = "select count(e) " +
                "from Ubicacion e  " +
                "where e.nombreUbicacion =:ide "
        val query = session.createQuery(hql)
        query.setParameter("ide", nombre)
        var valor = query.singleResult as Long
        return !valor.equals(0L)
    }

    override fun recuperarATodas(): List<Ubicacion> {
        val session = TransactionRunner.currentSession
        val hql = "from Ubicacion i " + "order by i.id asc"

        val query = session.createQuery(hql, Ubicacion::class.java)
        return query.resultList
    }

    override fun vectoresInfectadosDe(nombre:String): List<Vector> {
        val session = TransactionRunner.currentSession
        val hql = "select v from Ubicacion u " +
                "join u.vectores v " +
                "join v.especies e where u.nombreUbicacion=:nombre " +
                "group by v.id " +
                "having count(e)>0"

        val query = session.createQuery(hql, Vector::class.java)
        query.setParameter("nombre", nombre)
        return query.resultList
    }

    override fun cantidadDeVectoresPresentes(nombreUbicacion: String): Int {
        val session = TransactionRunner.currentSession
        val hql = "select count(v) from Ubicacion u " +
                "join u.vectores v on u.nombreUbicacion=:nombre "


        val query = session.createQuery(hql)
        query.setParameter("nombre", nombreUbicacion)
        var  valor = query.singleResult as Long
        return valor.toInt()
    }

    override fun cantidadDeVectoresInfectados(nombreUbicacion: String): Int {
        val session = TransactionRunner.currentSession
        val hql = "select count(distinct v) from Ubicacion u " +
                "join u.vectores v " +
                "join v.especies e where u.nombreUbicacion=:nombre "


        val query = session.createQuery(hql)
        query.setParameter("nombre", nombreUbicacion)
        var  valor = query.singleResult as Long
        return valor.toInt()
    }

    override fun especieMasInfecciosa(nombreUbicacion: String): String {
        val session = TransactionRunner.currentSession
        val hql = "select e.nombre " +
                "from Ubicacion u  " +
                "join u.vectores v " +
                "join v.especies e where u.nombreUbicacion=:nombre " +
                "group by e.nombre " +
                "order by count(e.nombre) desc"

        val query = session.createQuery(hql, String::class.java)
        query.setParameter("nombre",nombreUbicacion)
        query.maxResults=1
        if(query.resultList.size == 0 ){
            return ""
        }
        return query.resultList.get(0)
    }

    override fun recuperarTodosVectores(nombreUbicacion:String):List<Vector>{
        val session = TransactionRunner.currentSession
        val hql = "select v from Ubicacion u " +
                "join u.vectores v on u.nombreUbicacion=:nombre "


        val query = session.createQuery(hql, Vector::class.java)
        query.setParameter("nombre", nombreUbicacion)
        return query.resultList
    }

    override fun esEspeciePandemia(especieId: Int): Boolean {
        val session = TransactionRunner.currentSession
        val hql = "select count(distinct u) from Ubicacion u " +
                "join u.vectores v " +
                "join v.especies e where e.id=:especieId "

        val query = session.createQuery(hql)
        query.setParameter("especieId", especieId)
        val res = (query.singleResult as Long).toInt()
        val todos = this.cantUbicaciones()
        return (todos / 2) < res
    }

    fun cantUbicaciones(): Int{
        val session = TransactionRunner.currentSession
        val hql = "select count(e) " +
                "from Ubicacion e  "

        val query = session.createQuery(hql)
        return (query.singleResult as Long).toInt()
    }

   override fun cantDeEspecieEnU(nombreUbicacion: String,especieN:String):Int{
        val session = TransactionRunner.currentSession
        val hql = "select count(e) from Ubicacion u " +
                "join u.vectores v " +
                "join v.especies e where u.nombreUbicacion=:nombreU AND e.nombre=:nombreE "
               // "group by e.nombre " +
               // "having u.nombreUbicacion=:nombreU"
        val query = session.createQuery(hql)
        query.setParameter("nombreU", nombreUbicacion)
        query.setParameter("nombreE", especieN)
        var  valor = query.singleResult as Long
        return valor.toInt()
    }

}
