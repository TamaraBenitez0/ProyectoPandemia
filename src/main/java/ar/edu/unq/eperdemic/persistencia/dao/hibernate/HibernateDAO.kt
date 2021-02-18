package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import exceptions.ErrorRecuperar


open class HibernateDAO<T>(val entityType: Class<T>) {

    fun guardar(item: T) {
        val session = TransactionRunner.currentSession
        session.save(item)
    }


     fun actualizar(item: T){
        val session = TransactionRunner.currentSession
        session.update(item)
    }

     fun borrar(item: T){
        val session = TransactionRunner.currentSession
        session.delete(item)
    }

    fun recuperar(id: Int): T {
        val session = TransactionRunner.currentSession
      if( session.get(entityType, id)==null){
          throw ErrorRecuperar()
      }
        return session.get(entityType, id)
    }

}