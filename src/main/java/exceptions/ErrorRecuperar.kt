package exceptions

class ErrorRecuperar(): RuntimeException() {
    override val message: String?
        get() = "no se puede recuperar, no existe la identificacion"
}