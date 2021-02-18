package neo4j

import ar.edu.unq.eperdemic.modelo.Ubicacion
import org.junit.After
import org.junit.Assert
import org.junit.Test

class UbicacionNeoDaoTest {

    val dao: UbicacionNeoDao = UbicacionNeo4jDAO()

    @Test
    fun seCreaYSeRecuperaUnaUbicacion() {

        var bsAs: Ubicacion = Ubicacion("BsAs")
        dao.crear(bsAs)
        Assert.assertTrue(dao.existeUbicacion("BsAs"))
    }

    @Test
    fun seConectaUnaUbicacionConOtra() {

        var french: Ubicacion = Ubicacion("Francia")
        var italy: Ubicacion = Ubicacion("Italia")
        dao.crear(french)
        dao.crear(italy)
        dao.conectarUbicacion("Francia","Italia","Maritimo")
        Assert.assertTrue(dao.existeRelacion("Francia","Italia","Maritimo"))
    }

    @Test
    fun seLlegaAOtraUbicacionPorAlgunCamino(){
        var bsAs: Ubicacion = Ubicacion("buenos aires")
        var tokio: Ubicacion = Ubicacion("tokio")
        var madrid:Ubicacion=Ubicacion("madrid")
        dao.crear(bsAs)
        dao.crear(tokio)
        dao.crear(madrid)
        dao.conectarUbicacion("tokio","buenos aires","Aereo")
        dao.conectarUbicacion("buenos aires","madrid","Aereo")


        Assert.assertTrue(dao.sePuedeLlegarAUbicacion("tokio","Aereo","madrid"))
    }

    @Test
    fun sePuedeAlcanzarEn1Movimiento(){
        var bsAs: Ubicacion = Ubicacion("buenos aires")
        var tokio: Ubicacion = Ubicacion("tokio")
        dao.crear(bsAs)
        dao.crear(tokio)
        dao.conectarUbicacion("buenos aires","tokio","Aereo")
        Assert.assertTrue(dao.puedeLlegarEnUnMovimiento("buenos aires","Aereo","tokio"))
    }

    @Test
    fun seConsigueLosConectadosAUnaUbicacion(){

        var french: Ubicacion = Ubicacion("Francia")
        var italy: Ubicacion = Ubicacion("Italia")
        dao.crear(french)
        dao.crear(italy)
        dao.conectarUbicacion("Francia","Italia","Terrestre")
        Assert.assertEquals("Italia",dao.conectados("Francia").get(0))
        Assert.assertEquals(1,dao.conectados("Francia").size)
    }

    @Test
    fun noHayNingunoConectado(){

        var colombia: Ubicacion = Ubicacion("Colombia")
        var italy: Ubicacion = Ubicacion("Italia")
        dao.crear(colombia)
        dao.crear(italy)
        Assert.assertEquals(0,dao.conectados("Italia").size)
    }

    @Test
    fun getCaminoMasCortoEntreDosUbicaciones(){
        val madrid: Ubicacion = Ubicacion("Madrid")
        val tokio: Ubicacion = Ubicacion("Tokio")
        val pekin: Ubicacion = Ubicacion("Pekin")
        val paris: Ubicacion = Ubicacion("Paris")
        val roma: Ubicacion = Ubicacion("Roma")
        val australia: Ubicacion = Ubicacion("Australia")
        val  china: Ubicacion = Ubicacion("China")

        dao.crear(madrid)
        dao.crear(tokio)
        dao.crear(pekin)
        dao.crear(paris)
        dao.crear(roma)
        dao.crear(australia)
        dao.crear(china)

        dao.conectarUbicacion("Madrid","Tokio","Aereo")
        dao.conectarUbicacion("Madrid","Pekin","Terrestre")
        dao.conectarUbicacion("Tokio","Paris","Aereo")
        dao.conectarUbicacion("Paris","Roma","Aereo")
        dao.conectarUbicacion("Pekin","Australia","Aereo")
        dao.conectarUbicacion("Australia","China","Maritimo")
        dao.conectarUbicacion("China","Roma","Terrestre")

        val camino = dao.moverMasCorto("Madrid","Roma","Aereo|Maritimo|Terrestre")
        Assert.assertEquals(camino.get(0),"Madrid")
        Assert.assertEquals(camino.get(1),"Tokio")
        Assert.assertEquals(camino.size,4)

    }

    @Test
    fun gerCantidadDeLocalizacionAlcanzables(){
        val madrid: Ubicacion = Ubicacion("Madrid")
        val tokio: Ubicacion = Ubicacion("Tokio")
        val pekin: Ubicacion = Ubicacion("Pekin")
        val paris: Ubicacion = Ubicacion("Paris")
        val roma: Ubicacion = Ubicacion("Roma")

        dao.crear(madrid)
        dao.crear(tokio)
        dao.crear(pekin)
        dao.crear(paris)
        dao.crear(roma)

        dao.conectarUbicacion("Madrid","Tokio","Aereo")
        dao.conectarUbicacion("Madrid","Pekin","Terrestre")
        dao.conectarUbicacion("Tokio","Paris","Aereo")
        dao.conectarUbicacion("Paris","Roma","Aereo")
        dao.conectarUbicacion("Pekin","Roma","Maritimo")

        var cantidad = dao.capacidadDeExpansion("Madrid","Aereo",1)

        Assert.assertEquals(1,cantidad)

    }

    @After
    fun cleanNeo4j(){
       dao.clear()
    }

}