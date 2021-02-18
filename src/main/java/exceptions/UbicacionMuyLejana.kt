package exceptions

class UbicacionMuyLejana(): RuntimeException() {
    override val message: String?
        get() = "la ubicacion se encuentra muy lejos "
}