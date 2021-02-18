package neo4j
import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Ubicacion
import org.neo4j.driver.*

class UbicacionNeo4jDAO:UbicacionNeoDao {

    private val driver: Driver

    init {
        val env = System.getenv()
        val url = env.getOrDefault("NEO4J_URL", "bolt://localhost:7687")
        val username = env.getOrDefault("NEO4J_USER", "neo4j")
        val password = env.getOrDefault("NEO4J_PASSWORD", "root")

        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password),
                Config.builder().withLogging(Logging.slf4j()).build()
        )
    }

    override fun crear(ubicacion: Ubicacion) {
        driver.session().use { session ->
            session.writeTransaction {
                val query = "MERGE (n:Ubicacion {nombreUbicacion: ${'$'}elNombreUbicacion })"
                it.run(query, Values.parameters(
                        "elNombreUbicacion", ubicacion.nombreUbicacion
                ))
            }
        }
    }

    override fun conectarUbicacion(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}nombre1})
                MATCH (u2:Ubicacion {nombreUbicacion: ${'$'}nombre2})
                MERGE (u1)-[:$tipoCamino]->(u2)
                
            """
            session.run(
                    query, Values.parameters(
                    "nombre1", ubicacion1,
                    "nombre2", ubicacion2


            )
            )
        }


    }

    override fun existeUbicacion(nombreUbicacion: String): Boolean {

       return driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}nombre1})
                RETURN u1
                
            """
            val resultado = session.run(
                    query, Values.parameters(
                    "nombre1", nombreUbicacion

            )
            )
          resultado.list().size==1
        }


    }

    override fun existeRelacion(nombreUbicacion1: String, nombreUbicacion2: String, tipoCamino: String): Boolean {

        return driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}nombre1})
                MATCH (u2:Ubicacion {nombreUbicacion: ${'$'}nombre2})
                MATCH (u1)-[r:$tipoCamino]->(u2)
                RETURN r
                
            """
            val resultado = session.run(
                    query, Values.parameters(
                    "nombre1", nombreUbicacion1,
                    "nombre2", nombreUbicacion2
            )
            )
            resultado.list().size==1
        }

    }

//retorna si la ubicacion puede alcanzarse
  override fun sePuedeLlegarAUbicacion(actual: String, tipo:String, ubicacionAMover:String):Boolean {
        return driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}nombre1})
                MATCH (u2:Ubicacion {nombreUbicacion: ${'$'}nombre2})
                MATCH (u1)-[r: $tipo*..]->(u2)
                RETURN u2
                
            """
            val resultado = session.run(
                    query, Values.parameters(
                    "nombre1", actual,
                    "nombre2", ubicacionAMover
            )
            )
            resultado.list().size >0

        }
    }

    //retorna si la ubicacion puede ser alcanzada por el tipo donde el vector se mueve en 1 movimiento
   override fun puedeLlegarEnUnMovimiento(actual: String, tipo:String, ubicacionAMover:String):Boolean {
        return driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}nombre1})
                MATCH (u2:Ubicacion {nombreUbicacion: ${'$'}nombre2})
                MATCH (u1)-[r: $tipo*..1]->(u2)
                RETURN u2
                
            """
            val resultado = session.run(
                    query, Values.parameters(
                    "nombre1", actual,
                    "nombre2", ubicacionAMover
            )
            )
            resultado.list().size ==1

        }
    }
    //retorna los nombres de las ubicacion que dicha ubicacion esta conectada
   override fun conectados(nombreUbicacion: String):List<String>{
        return driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}nombre1})
                MATCH (u1)-[r:Terrestre|Maritimo|Aereo]->(u2)
                RETURN u2
                
            """
            val resultado = session.run(
                    query, Values.parameters(
                    "nombre1", nombreUbicacion
            )
            )
            resultado.list{record:Record->
                val valor=record[0]
                valor["nombreUbicacion"].asString()
            }

        }
    }

    override fun moverMasCorto(ubicacionActual: String, nombreDeUbicacion: String,unCamino:String): List<String> {
        return driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}ubicacion1})
                MATCH (u2:Ubicacion {nombreUbicacion: ${'$'}ubicacion2})
                MATCH p = shortestPath((u1)-[c: $unCamino*]->(u2))
                UNWIND nodes(p) as ubicaciones 
                RETURN  ubicaciones
            """
            val resultado = session.run(
                    query, Values.parameters(
                    "ubicacion1", ubicacionActual,
                    "ubicacion2", nombreDeUbicacion
            )
            )
            resultado.list{record:Record ->
                val u = record[0]
                u["nombreUbicacion"].asString()
            }
        }
    }

    override fun capacidadDeExpansion(ubicacion: String,unCamino: String, movimientos:Int): Int{
        return driver.session().use { session ->
            val query = """
                MATCH (u1:Ubicacion {nombreUbicacion: ${'$'}ubicacion})
                MATCH (u1)-[r:$unCamino*..$movimientos ]->(ubs)
                RETURN ubs
            """
            val resultado = session.run(
                    query, Values.parameters(
                    "ubicacion", ubicacion
                    
            )
            )
            resultado.list().size
            }
        }


   override fun clear() {
        return driver.session().use { session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }
}

