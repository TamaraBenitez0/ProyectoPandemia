package exceptions

class ErrorNotLider():RuntimeException() {
    override val message: String?
        get() = "No existe una especie lider"
}