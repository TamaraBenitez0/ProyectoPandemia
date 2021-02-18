package ar.edu.unq.eperdemic.modelo

import org.influxdb.annotation.Column
import org.influxdb.annotation.Measurement
import java.time.Instant

@Measurement(name="ubicaciones")
class MedicionUbicaciones {

    @Column(name="time")
    var tiempo: Instant?=null

    @Column(name="contagios")
    var contagios : Int ?=null

    @Column(name="cantidadDeVectores")
    var cantVectores:Int?=null

    @Column(name="TipoEspecie")
    var tipoEspecie:String?=null

    @Column(name="PromedioDeInfectados")
    var promedio:Double?=null
}