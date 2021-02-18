package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VectorDAOTest {

    var dao: VectorDAO = HibernateVectorDAO()
    val ubicacionDao=HibernateUbicacionDAO()
    val especieDAO=HibernateEspecieDAO()
    val dataService=DataServiceHibernate()

    @Before
    fun setUp() {
        dataService.crearSetDeDatosIniciales()
    }

    @Test
    fun seGuardaUnVector() {
        var vector =Vector(VectorFrontendDTO.TipoDeVector.Animal, "madrid")

       var  vectorEQ:Vector=runTrx {  dao.crearVector(vector)}

        Assert.assertSame(vector,vectorEQ)

    }

    @Test
    fun seRecuperaUnVector() {
        //el primer vector es persona
        val rec=runTrx { dao.recuperar(1) }
        Assert.assertTrue(rec.esPersona())
    }

    @Test
    fun seActualizaUnVector() {
        var vector =Vector(VectorFrontendDTO.TipoDeVector.Animal,"buenos aires")
        runTrx { dao.crearVector(vector) }
        runTrx {  ubicacionDao.guardar(Ubicacion("misiones"))}
        vector.setUbicacion("misiones")
        runTrx { dao.actualizar(vector) }


        val rec=runTrx { dao.recuperar(9) }

        Assert.assertEquals("misiones", rec.lugarDeResidencia())
    }


    @Test
    fun unVectorNoTieneEnfermedades() {

        var vec:Vector= Vector(VectorFrontendDTO.TipoDeVector.Insecto,"madrid")
        runTrx {dao.crearVector(vec)}
        var recuperado:Vector= runTrx{dao.recuperar(9)}
        Assert.assertFalse(recuperado.estaInfectado())

    }

    @Test
    fun unVectorTieneEnfermedades(){
        var vector:Vector = Vector(VectorFrontendDTO.TipoDeVector.Animal,"madrid")
        var especie:Especie= Especie()
        especie.set("covid-19") // hecho para testear la infeccion del vector
        runTrx { especieDAO.guardar(especie)}
        vector.agregarEspecie(especie)
        runTrx { dao.crearVector(vector) }
        var cant=runTrx{dao.recuperar(9)}
        Assert.assertEquals(1,cant.especies.size) //traigo la lista a memoria solo para testear
    }

    @Test
    fun sePruebaQueEsLaEspecieLaQueInfecto(){ //test solo para probar que garantize que se agrega la especie correcta
        var vector:Vector = Vector(VectorFrontendDTO.TipoDeVector.Animal, "madrid")
        var especie:Especie= Especie()
        especie.set("covid-19") // hecho para testear la infeccion del vector
        runTrx { especieDAO.guardar(especie)}
        vector.agregarEspecie(especie)
        runTrx { dao.crearVector(vector) }
        var rec:Vector =runTrx { dao.recuperar(9) }
        var especiesList=rec.especies.toList()
        Assert.assertEquals("covid-19",especiesList.get(0).nombre)
    }

    @After
    fun cleanUp(){
        dataService.eliminarTodo()
    }
}