package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.RequerimientosMutacion
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.RequerimientosMutacionDAO

class HibernateRequerimientosMutacionDAO: HibernateDAO<RequerimientosMutacion> (RequerimientosMutacion::class.java), RequerimientosMutacionDAO {


}