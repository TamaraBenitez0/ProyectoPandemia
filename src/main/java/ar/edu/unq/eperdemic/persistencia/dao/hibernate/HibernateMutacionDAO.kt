package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernateMutacionDAO : HibernateDAO<Mutacion>(Mutacion::class.java), MutacionDAO {

    override fun crearMutacion(mutacion: Mutacion): Mutacion {
        val session = TransactionRunner.currentSession
        session.save(mutacion)
        return mutacion
    }



}