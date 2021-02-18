package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.services.runner.TransactionRunner


interface UbicacionDAO {
    fun guardar(ubi:Ubicacion)
    fun recuperar(nombre:String):Ubicacion
    fun actualizar(ubi:Ubicacion)
    fun recuperarATodas(): List<Ubicacion>
    fun borrar(ubi:Ubicacion)
    fun recuperarTodosVectores(nombreUbicacion:String):List<Vector>
    fun vectoresInfectadosDe(nombre:String): List<Vector>
    fun cantidadDeVectoresPresentes(nombreUbicacion:String):Int
    fun cantidadDeVectoresInfectados(nombreUbicacion:String):Int
    fun especieMasInfecciosa(nombreUbicacion:String):String
    fun esEspeciePandemia(especieId: Int): Boolean
    fun cantDeEspecieEnU(nombreUbicacion: String,especieN:String):Int
}