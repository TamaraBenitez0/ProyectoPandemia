package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCConnector.execute
import java.sql.Connection
import java.sql.PreparedStatement
import kotlin.collections.*
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAOError
class JDBCPatogenoDAO  {

   fun crear(patogeno: Patogeno): Int {
        try{return execute { conn: Connection ->
            val ps =
                    conn.prepareStatement("INSERT INTO patogeno ( tipo, cantidadDeEspecies) VALUES (?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS)
            ps.setString(1, patogeno!!.tipo)
   //         ps.setInt(2, patogeno.cantidadDeEspecies)
            ps.execute()
            var res =ps.generatedKeys
            var id:Int? =null
            while(res.next()){
                if(res.isLast){
                    id= res.getInt(1)
                }
            }
            ps.close()
            id!!
        }}catch (e : Exception){
            throw JDBCPatogenoDAOError("No se inserto el patogeno con nombre $patogeno")
        }
    }


     fun actualizar(patogeno: Patogeno) {

        if(patogeno.id == null){
            throw JDBCPatogenoDAOError("El patogeno a actualizar no tiene id")
        }
            execute{conn:Connection ->
            val ps = conn.prepareStatement("UPDATE patogeno  SET tipo =?, cantidadDeEspecies =? WHERE id=? ")
            ps.setString(1, patogeno.tipo)
     //       ps.setInt(2, patogeno.cantidadDeEspecies)
            ps.setInt(3,patogeno.id!!)

            ps.executeUpdate()
            if(ps.updateCount < 1){
                throw JDBCPatogenoDAOError("El patogeno a actualizar no esta registrado")
            }
                ps.close()
            null
            }
        }

     fun recuperar(patogenoId: Int): Patogeno {
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT id, tipo, cantidadDeEspecies FROM patogeno WHERE id = ?")
            ps.setInt(1, patogenoId)
            val resultSet = ps.executeQuery()
            var patogeno: Patogeno? = null
            while(resultSet.next()) {
                patogeno = Patogeno(resultSet.getString("tipo"))
       //         patogeno.cantidadDeEspecies = resultSet.getInt("cantidadDeEspecies")
                patogeno.id = resultSet.getInt("id")

            }
            if(patogeno == null) {
                throw JDBCPatogenoDAOError("No existe el patogeno con el id $patogenoId")
            }
            ps.close()
            patogeno!!
        }
    }

     fun recuperarATodos(): List<Patogeno> {
        var res:MutableList<Patogeno> = mutableListOf()
        return execute { conn: Connection ->
            val ps = conn.prepareStatement("SELECT id, tipo, cantidadDeEspecies FROM patogeno ORDER BY  tipo ASC,cantidadDeEspecies ASC") //el id ya se posiciona asc
            val resultSet = ps.executeQuery()
            while (resultSet.next()) {
                var recuperado = Patogeno(resultSet.getString("tipo"))
                recuperado.setID(resultSet.getInt("id"))
         //       recuperado.setEspecies(resultSet.getInt("cantidadDeEspecies"))
                res.add(recuperado)
            }
            ps.close()
            res
        }
    }


     fun vaciarTabla() {
        execute { conn: Connection ->
            val ps = conn.prepareStatement("TRUNCATE TABLE epers_tp1_jdbc.patogeno")
            ps.execute()
            ps.close()
        }
    }

     fun cantidadDeVectoresInfectados(id: Int): Int {
        return 1
    }

    init {
            val initializeScript = javaClass.classLoader.getResource("createAll.sql").readText()
            execute {
                val ps = it.prepareStatement(initializeScript)
                ps.execute()
                ps.close()
                null
            }
        }
}


