package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.AtributoPatogeno
import ar.edu.unq.eperdemic.persistencia.dao.AtributoPatogenoDAO

class HibernateAtributoPatogenoDao :HibernateDAO<AtributoPatogeno>(AtributoPatogeno::class.java), AtributoPatogenoDAO  {
}