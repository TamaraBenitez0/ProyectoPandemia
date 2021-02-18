package exceptions

class UbicacionNoAlcanzable(): RuntimeException() {
    override val message: String?
        get() = "no se puede alcanzar la ubicacion "
}