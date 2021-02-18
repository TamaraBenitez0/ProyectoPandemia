package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieDAO {
    fun guardar(e: Especie)
    fun recuperar(id:Int): Especie
    fun actualizar(e:Especie)
    fun infectoAMasHumanos(tipo: VectorFrontendDTO.TipoDeVector) : Especie
    fun lideresEspecie(tipoH: VectorFrontendDTO.TipoDeVector,tipoA: VectorFrontendDTO.TipoDeVector):List<Especie>
}