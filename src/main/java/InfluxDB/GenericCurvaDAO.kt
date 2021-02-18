package InfluxDB

import ar.edu.unq.eperdemic.modelo.MedicionUbicaciones
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import org.influxdb.dto.Point.Builder
import org.influxdb.dto.Query
import org.influxdb.dto.QueryResult
import org.influxdb.impl.InfluxDBResultMapper

class GenericCurvaDAO: CurvaDAO {
        var influxDB: InfluxDB
        var dbName: String? = null
        private var retention: String? = null

        init {
            val serverURL = "http://localhost:8086"
            val username = "root"
            val password = "root"
            influxDB = InfluxDBFactory.connect(serverURL, username, password)
            retention = "aRetentionPolicy"
            dbName = "prueba"
            influxDB.createDatabase(dbName)
            influxDB.setDatabase(dbName)
            influxDB.createRetentionPolicy(retention, dbName, "30d", "30m", 1, true)
            influxDB.setRetentionPolicy(retention)
        }

        //batchPoints = BatchPoints.database(dbName).retentionPolicy(rpName).consistency(ConsistencyLevel.ALL).build()

        override fun obtenerMediciones(nombre: String, nombreE:String): List<MedicionUbicaciones> {
            var command ="SELECT * FROM ubicaciones WHERE nombreUbicacion='$nombre' AND TipoEspecie='$nombreE' "
            var result = InfluxDBResultMapper()
            var pointsResult= result.toPOJO(this.query(command), MedicionUbicaciones::class.java)
            return pointsResult
        }


        override fun borrar() {
            influxDB.dropRetentionPolicy(retention, dbName)
            influxDB.deleteDatabase(dbName)
        }

        override fun query(command: String?): QueryResult {
            return influxDB!!.query(Query(command, dbName))
        }


        override fun write(point:Point){
            influxDB.write(point)
            influxDB.close()
        }

    }
