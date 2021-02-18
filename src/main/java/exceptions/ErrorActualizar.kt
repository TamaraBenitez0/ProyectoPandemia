package exceptions

class ErrorActualizar():RuntimeException() {
    override val message: String?
    get() = "no se puede actualizar, no existe la identificacion"
}