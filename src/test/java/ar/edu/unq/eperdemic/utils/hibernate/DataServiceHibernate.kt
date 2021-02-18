package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.jdbc.RequerimientosMutacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import ar.edu.unq.eperdemic.utils.DataService
import neo4j.UbicacionNeo4jDAO
import neo4j.UbicacionNeoDao

class DataServiceHibernate : DataService {

    var especieDAO : EspecieDAO = HibernateEspecieDAO()
    var patogenoDAO : PatogenoDAO = HibernatePatogenoDAO()
    var vectorDAO : VectorDAO = HibernateVectorDAO()
    var ubicacionDAO : UbicacionDAO = HibernateUbicacionDAO()
    var dataDAO : DataDAO = HibernateDataDAO()
    var mutacionDao : MutacionDAO = HibernateMutacionDAO()
    var daoR : RequerimientosMutacionDAO = HibernateRequerimientosMutacionDAO()
    var ubiNeoDao:UbicacionNeoDao=UbicacionNeo4jDAO()
    val atribSumarDao=HibernateAtributoPatogenoDao()

    override fun crearSetDeDatosIniciales() {
        val bsAs: Ubicacion = Ubicacion("buenos aires")
        val pekin: Ubicacion = Ubicacion("pekin")
        val tokio: Ubicacion = Ubicacion("tokio")
        val moscu: Ubicacion = Ubicacion("moscu")
        val madrid: Ubicacion = Ubicacion("madrid")
        runTrx {
            ubicacionDAO.guardar(bsAs) ; ubiNeoDao.crear(bsAs)
            ubicacionDAO.guardar(pekin); ubiNeoDao.crear(pekin)
            ubicacionDAO.guardar(tokio);ubiNeoDao.crear(tokio)
            ubicacionDAO.guardar(moscu);ubiNeoDao.crear(moscu)
            ubicacionDAO.guardar(madrid); ubiNeoDao.crear(madrid)
        }

        var atrib= AtributoPatogeno()
        var atrib2=AtributoPatogeno()
        atrib.capacidadContagioInsecto=100 //seteo de valor para pruebas
        runTrx { atribSumarDao.guardar(atrib);atribSumarDao.guardar(atrib2) }
        val bacteria = Patogeno("Bacteria")
        bacteria.setAts(atrib)
        val hongo = Patogeno("Virus")
        hongo.setAts(atrib2)
        runTrx {
            patogenoDAO.crear(bacteria) //id=1
            patogenoDAO.crear(hongo)//id=2
        }

        val dengue = Especie(bacteria, "dengue", "brasil")
        val covid = Especie(bacteria, "covid", "brasil")
        val rabia = Especie(hongo, "rabia", "brasil")
        val gripe = Especie(hongo, "gripe", "brasil")
        val zika = Especie(bacteria, "zika", "brasil")
        dengue.adn=1
        rabia.adn = 4
        gripe.adn = 42
        runTrx{
            especieDAO.guardar(dengue)//id=1
            especieDAO.guardar(covid)//id=2
            especieDAO.guardar(rabia)//id=3
            especieDAO.guardar(gripe)//id=4
            especieDAO.guardar(zika)//id=5
        }

        val insecto = Vector(VectorFrontendDTO.TipoDeVector.Insecto,"madrid")
        val animal = Vector(VectorFrontendDTO.TipoDeVector.Animal,"madrid")
        val persona = Vector(VectorFrontendDTO.TipoDeVector.Persona, "pekin")
        val pequines= Vector(VectorFrontendDTO.TipoDeVector.Insecto,"pekin")
        val personaEnf = Vector(VectorFrontendDTO.TipoDeVector.Persona, "buenos aires")
        val animalEnf=Vector(VectorFrontendDTO.TipoDeVector.Animal, "buenos aires")
        val insectoEnf=   Vector(VectorFrontendDTO.TipoDeVector.Insecto, "tokio")
        val personaTurista = Vector(VectorFrontendDTO.TipoDeVector.Persona,"buenos aires")
        pequines.agregarEspecie(zika)
        personaEnf.agregarEspecie(covid)
        animalEnf.agregarEspecie(rabia)
        animalEnf.agregarEspecie(covid)
        insectoEnf.agregarEspecie(dengue)
        runTrx {
            vectorDAO.crearVector(personaEnf) //id=1
            vectorDAO.crearVector(insectoEnf)//id=2
            vectorDAO.crearVector(animalEnf)  //id=3
            vectorDAO.crearVector(insecto)//id=4
            vectorDAO.crearVector(animal)//id=5
            vectorDAO.crearVector(persona)//id=6
            vectorDAO.crearVector(personaTurista)//id=7
            vectorDAO.crearVector(pequines)//id=8
        }

        var requerimientos : RequerimientosMutacion = RequerimientosMutacion(2, hashSetOf())
        runTrx {daoR.guardar(requerimientos)}//id=1
        var mutacion : Mutacion = Mutacion (4, "Letalidad",requerimientos )
        runTrx { mutacionDao.crearMutacion(mutacion) }//id=1
        var requerimientos2= RequerimientosMutacion(1, hashSetOf(mutacion))
        runTrx {daoR.guardar(requerimientos2)}//id=2
        var mutacion2 : Mutacion = Mutacion (4, "Defensa",requerimientos2 )
        runTrx { mutacionDao.crearMutacion(mutacion2) }//id=2
        //transacciones por legibilidad
    }

    override fun eliminarTodo() {
        runTrx {dataDAO.clear()}
    }
}