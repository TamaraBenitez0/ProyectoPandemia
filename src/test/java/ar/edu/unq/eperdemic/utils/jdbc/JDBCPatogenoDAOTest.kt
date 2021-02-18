
package ar.edu.unq.eperdemic.utils.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAOError
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class JDBCPatogenoDAOTest {

    private val dao: JDBCPatogenoDAO = JDBCPatogenoDAO()
    lateinit var patogeno: Patogeno

    @Before
    fun setUp() {
        var gripe = Patogeno("asda")
        var sarampion = Patogeno("as")
        var paperas =  Patogeno("aw")
        dao.crear(gripe)
        dao.crear(sarampion)
        dao.crear(paperas)
    }

    @After
    fun vaciarBaseDeDatos() {
        dao.vaciarTabla()
    }


    @Test
    fun seObtieneElIdDelPatogenoPersistidoEnLaBaseDeDatos() {
        val hongo = Patogeno("Hongo")
        val idHongo = dao.crear(hongo)
        Assert.assertEquals(idHongo, 4)
        val bacteria = Patogeno("Bacteria")
        val idBacteria = dao.crear(bacteria)
        Assert.assertEquals(idBacteria, 5)
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun alQuererPersistirUnPatogenoConTipoExistenteDevuelveUnError() {
        var patogeno1: Patogeno = Patogeno("asda")
        dao.crear(patogeno1)
    }

    @Test
    fun seRecuperaElPatogenoConId() {
        patogeno = dao.recuperar(1)
        Assert.assertEquals(1, patogeno.id)
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun alQuererRecuperarUnPatogenoInexistenteDevuelveUnError() {
        patogeno = dao.recuperar(10)
    }

    @Test
    fun seActualizaElPatogeno() {
        patogeno = dao.recuperar(1)
        patogeno.tipo = "sss"
        dao.actualizar(patogeno)
        Assert.assertEquals("sss", dao.recuperar(1).tipo)
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun seIntentaActualizazUnPatogenoSinIdYLanzaUnaExcepcion() {
        val hongo = Patogeno("Hongo")
        dao.actualizar(hongo)
    }

    @Test(expected = JDBCPatogenoDAOError::class)
    fun seIntentaActualizazUnPatogenoSinRegistrarYLanzaUnaExcepcion() {
        val hongo = Patogeno("Hongo")
        hongo.setID(43)
        dao.actualizar(hongo)
    }

    @Test
    fun seRecuperaTodosLosPatogenosPersistidosAlfabeticamente() {
        val listaDePatogenos: List<Patogeno>
        val hongo = Patogeno("Hongo")
        val idHongo = dao.crear(hongo)
        val bacteria = Patogeno("Bacteria")
        val idBacteria = dao.crear(bacteria)
        listaDePatogenos = dao.recuperarATodos()
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
        dao.vaciarTabla()
        listaDePatogenos = dao.recuperarATodos()
        Assert.assertEquals(listaDePatogenos.size, 0)
    }

}