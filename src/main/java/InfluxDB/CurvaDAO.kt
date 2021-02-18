package InfluxDB

import ar.edu.unq.eperdemic.modelo.MedicionUbicaciones
import org.influxdb.dto.Point
import org.influxdb.dto.QueryResult

interface CurvaDAO {

    fun obtenerMediciones(nombre: String, nombreE:String): List<MedicionUbicaciones>
    fun write(point: Point)
    fun query(command: String?): QueryResult
    fun borrar()
}