package mongo

import InfluxDB.GenericCurvaDAO
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import ar.edu.unq.eperdemic.utils.hibernate.DataServiceHibernate
import mongoDb.EventoMongoDao
import mongoDb.MongoEventoDAO
import neo4j.UbicacionNeo4jDAO
import neo4j.UbicacionNeoDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FeedServiceTest {
    var curva : GenericCurvaDAO = GenericCurvaDAO() //por ahora uso la clase concreta para testear

    val especieDao: EspecieDAO = HibernateEspecieDAO()
    val vectorDao : VectorDAO = HibernateVectorDAO()
    val ubiDao: UbicacionDAO = HibernateUbicacionDAO()
    val daoNeo: UbicacionNeoDao = UbicacionNeo4jDAO()
    val evento:MongoEventoDAO=EventoMongoDao()
    val feedService: FeedService = FeedServiceImpl(evento)
    val vectorService: VectorService = VectorServiceImpl(vectorDao,evento,ubiDao)
    val ubicacionService: UbicacionService = UbicacionServiceImpl(vectorDao,ubiDao,daoNeo,evento,curva)
    val dataService: DataService = DataServiceHibernate()
    val patogenoDao: PatogenoDAO = HibernatePatogenoDAO()
    val mutacionDao: MutacionDAO = HibernateMutacionDAO()
    val patogenoService: PatogenoService = PatogenoServiceImpl(patogenoDao,especieDao,ubiDao,evento)
    val mutacionService: MutacionService = MutacionServiceImpl(mutacionDao,especieDao,patogenoDao,evento)

    @Before
    fun setUp(){
        dataService.crearSetDeDatosIniciales()
    }


    @Test
    fun noHayEventoParaElVectorConId() {
        Assert.assertEquals(0,feedService.feedVector(1).size)
    }

    @Test
    fun seGeneraUnEventoCuandoElVectorEsInfectado(){
        var vectorRecuperado = vectorService.recuperarVector(6)
        var especieRecuperada= runTrx {especieDao.recuperar(1)}
        Assert.assertEquals(0,feedService.feedVector(6).size)
        vectorService.infectar(vectorRecuperado,especieRecuperada)
        Assert.assertEquals(1,feedService.feedVector(6).size)
    }

    @Test
    fun seGeneraUnEventoCuandoElVectorEsContagiado(){

        var vector = vectorService.recuperarVector(6)
        var inf= vectorService.recuperarVector(2)
        Assert.assertEquals(0,feedService.feedVector(6).size)
        vectorService.contagiar(inf, listOf(vector))

        Assert.assertEquals(1,feedService.feedVector(6).size)
    }

    @Test
    fun seGeneran4EventosUnArriboY3Contagios(){

        ubicacionService.conectar("tokio","buenos aires","Aereo")
        Assert.assertEquals(0,feedService.feedVector(2).size)
        ubicacionService.mover(2,"buenos aires")
        Assert.assertEquals(4,feedService.feedVector(2).size)

    }

    @Test
    fun seTesteaElOrdenamientoDescendente(){
        ubicacionService.conectar("tokio","buenos aires","Aereo")
        Assert.assertEquals(0,feedService.feedVector(2).size)
        ubicacionService.mover(2,"buenos aires")
        var feedVector = feedService.feedVector(2)
        Assert.assertTrue(feedVector[0].fecha>feedVector[1].fecha)
        Assert.assertEquals(4,feedVector.size)
    }


    @Test
    fun seGeneraUnEventoSiElVectorExpandeElVirus() {

        vectorService.borrarVector(1)
        vectorService.borrarVector(3)
        vectorService.borrarVector(8)
        Assert.assertEquals(0,feedService.feedVector(6).size)
        ubicacionService.expandir("pekin")

        Assert.assertEquals(1,feedService.feedVector(6).size)
    }

    @Test
    fun noHayEventoParaLaUbicacion() {
        Assert.assertEquals(0,feedService.feedUbicacion("pekin").size)
    }

    @Test
    fun seGeneran4EventosUnArriboY3ContagiosEnLaUbicacion(){

        ubicacionService.conectar("tokio","buenos aires","Aereo")
        Assert.assertEquals(0,feedService.feedUbicacion("buenos aires").size)
        ubicacionService.mover(2,"buenos aires")
        var feedUbicacion = feedService.feedUbicacion("buenos aires")
        Assert.assertTrue(feedUbicacion[0].fecha>feedUbicacion[1].fecha)
        Assert.assertEquals(4,feedUbicacion.size)

    }

    @Test
    fun seGeneraUnEventoSiElVectorExpandeElVirusEnLaUbicacion() {

        vectorService.borrarVector(1)
        vectorService.borrarVector(3)
        vectorService.borrarVector(8)
        Assert.assertEquals(0,feedService.feedUbicacion("pekin").size)
        ubicacionService.expandir("pekin")

        Assert.assertEquals(1,feedService.feedUbicacion("pekin").size)
    }

    @Test
    fun noHayEventosDePatogenos(){
        Assert.assertEquals(0,feedService.feedPatogeno("Bacteria").size)
    }

    @Test

    fun seGeneraUnEventoCuandoSeCreaUnaNuevaEspecieParaElPatogenoBacteria(){
        Assert.assertEquals(0,feedService.feedPatogeno("Bacteria").size)
        patogenoService.agregarEspecie(1,"Salmonella","moscu")
        Assert.assertEquals(1,feedService.feedPatogeno("Bacteria").size)
        Assert.assertEquals("Se ha creado una nueva especie",
                            feedService.feedPatogeno("Bacteria").get(0).descripcion)
    }

    @Test
    fun seGeneraUnEventoCuandoMutaLaEspecieDelPatogenoVirus(){
        Assert.assertEquals(0,evento.getEventosDePatogeno("Virus").size)
        mutacionService.mutar(4,1)
        Assert.assertEquals(1,evento.getEventosDePatogeno("Virus").size)
        mutacionService.mutar(4,2)
        Assert.assertEquals(2,feedService.feedPatogeno("Virus").size)
    }


    //test extra
    @Test
    fun seGeneraUnEventoCuandoUnaEspecieSeVuelvePandemia(){
        var v1 = vectorService.recuperarVector(5)
        var v4 = vectorService.recuperarVector(7)
        var v3 = vectorService.recuperarVector(2)
        Assert.assertFalse(evento.esPandemiaRegistrada(1))
        vectorService.contagiar(v3, listOf(v1,v4))
       Assert.assertTrue(evento.esPandemiaRegistrada(1))
    }

    @Test
    fun unaEspecieNoEsPandemia(){
        var v1 = vectorService.recuperarVector(5)
        var v3 = vectorService.recuperarVector(2)
        vectorService.contagiar(v3, listOf(v1))
        Assert.assertFalse(evento.esPandemiaRegistrada(1))
    }

    @Test
    fun seGeneraUnEventoCuandoSeContagiaUnaEspecieEnUnaNuevaUbicacion(){
        ubicacionService.conectar("tokio","buenos aires","Aereo")
        Assert.assertEquals(0, feedService.feedPatogeno("Bacteria").size)
        ubicacionService.mover(2,"buenos aires")
        Assert.assertEquals(1, feedService.feedPatogeno("Bacteria").size)
    }

    @After
    fun cleanUp(){
        dataService.eliminarTodo()
        daoNeo.clear()
        evento.deleteAll()
    }

}