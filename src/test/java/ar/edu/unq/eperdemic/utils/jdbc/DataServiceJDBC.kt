package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.utils.DataService

class DataServiceJDBC : DataService {

  // var dao: PatogenoDAO = JDBCPatogenoDAO()

    override fun crearSetDeDatosIniciales() {
     //   var gripe = Patogeno("asda")
       // var sarampion = Patogeno("as")
        //var paperas =  Patogeno("aw")
        //dao.crear(gripe)
        //dao.crear(sarampion)
        //dao.crear(paperas)
    }

    override fun eliminarTodo() {
        //dao.vaciarTabla()
    }

}