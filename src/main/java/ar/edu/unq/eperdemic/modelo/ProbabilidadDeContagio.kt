package ar.edu.unq.eperdemic.modelo

open class ProbabilidadDeContagio {
//clase para "liberar" de esta responsabilidad al vector
    open fun calcularFactorContagio(especie:Especie,vector:Vector):Int{
        val random =(1..10).random()
        return random + especie.getFactorContagioSegunTipo(vector)
    }

    open fun getResultadoDeContagio(valor: Int) : Boolean {
        val total = (1..100).random()
        return total in (1..valor)
    }
}