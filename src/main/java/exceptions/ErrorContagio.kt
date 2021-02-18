package exceptions

class ErrorContagio():RuntimeException() {
    override val message: String?
        get() = "No se puede contagiar,el vector esta sano"
}