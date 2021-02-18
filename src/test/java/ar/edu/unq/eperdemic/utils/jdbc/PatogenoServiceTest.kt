
package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAOError
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.utils.DataService
import ar.edu.unq.eperdemic.utils.jdbc.DataServiceJDBC
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatogenoServiceTest {
/*
    private val dao: PatogenoDAO = HibernatePatogenoDAO()
    private val service: DataService = DataServiceJDBC()
    private val patogenoService = PatogenoServiceImpl(dao)
    private lateinit var patogeno: Patogeno

    @Before
    fun crearSetDeDatos() {
        service.crearSetDeDatosIniciales()
    }

    @After
    fun eliminarSetDeDatos() {
        service.eliminarTodo()
    }


    @Test
    fun seObtieneElIdDelPatogenoPersistidoEnLaBaseDeDatos() {
        val hongo = Patogeno("Hongo")
        val idHongo = patogenoService.crearPatogeno(hongo)
        Assert.assertEquals(idHongo, 4)
        val bacteria = Patogeno("Bacteria")
        val idBacteria = patogenoService.crearPatogeno(bacteria)
        Assert.assertEquals(idBacteria, 5)
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun alQuererPersistirUnPatogenoConTipoExistenteDevuelveUnError() {
        var patogeno1: Patogeno = Patogeno("asda")
        patogenoService.crearPatogeno(patogeno1)
    }

    @Test
    fun seRecuperaElPatogenoConId() {
        patogeno = patogenoService.recuperarPatogeno(1)
        Assert.assertEquals(1, patogeno.id)
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun alQuererRecuperarUnPatogenoInexistenteDevuelveUnError() {
        patogeno = patogenoService.recuperarPatogeno(10)
    }

    @Test
    fun seActualizaElPatogeno() {
        patogeno = dao.recuperar(1)
        patogeno.tipo = "sss"
        dao.actualizar(patogeno)
        Assert.assertEquals("sss", dao.recuperar(1).tipo)
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun seIntentaActualizazUnPatogenoSinId() {
        val hongo = Patogeno("Hongo")
        dao.actualizar(hongo)//preguntar por dao
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun seIntentaActualizazUnPatogenoSinRegistrar() {
        val hongo = Patogeno("Hongo")
        hongo.setID(43)
        dao.actualizar(hongo)
    }

        @Test
    fun seRecuperaTodosLosPatogenosPersistidosAlfabeticamente() {
        val listaDePatogenos: List<Patogeno>
        val hongo = Patogeno("Hongo")
        val idHongo = patogenoService.crearPatogeno(hongo)
        val bacteria = Patogeno("Bacteria")
        val idBacteria = patogenoService.crearPatogeno(bacteria)
        listaDePatogenos = patogenoService.recuperarATodosLosPatogenos()
        Assert.assertEquals(listaDePatogenos.size, 5)
        Assert.assertEquals("as", listaDePatogenos.get(0).tipo)
        Assert.assertEquals("asda", listaDePatogenos.get(1).tipo)
        Assert.assertEquals("aw", listaDePatogenos.get(2).tipo)
        Assert.assertEquals("Bacteria", listaDePatogenos.get(3).tipo)
        Assert.assertEquals("Hongo", listaDePatogenos.get(4).tipo)
    }

    @Test
    fun seIntentaRecuperaTodosLosPatogenosPersistidosAlfabeticamentePeroLaListaEstaVacia() {
        val listaDePatogenos: List<Patogeno>
        service.eliminarTodo()
        listaDePatogenos = patogenoService.recuperarATodosLosPatogenos()
        Assert.assertEquals(listaDePatogenos.size, 0)
    }

    @Test
    fun agregarUnaEspecieAlPatogeno(){
        var hongo = Patogeno("Hongo")
        patogenoService.crearPatogeno(hongo)
        var especie = patogenoService.agregarEspecie(4,"Wacuca","China")
        Assert.assertEquals(patogenoService.recuperarPatogeno(4).cantidadDeEspecies, 1)
        Assert.assertEquals("Wacuca",especie.nombre)
        Assert.assertEquals("China",especie.paisDeOrigen)
    }
*/
}
