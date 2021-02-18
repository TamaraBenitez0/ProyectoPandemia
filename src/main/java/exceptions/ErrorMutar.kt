package exceptions

class ErrorMutar():RuntimeException() {

    override val message: String?
    get() = "No se puede mutar, no se cumplen los requerimientos"
}