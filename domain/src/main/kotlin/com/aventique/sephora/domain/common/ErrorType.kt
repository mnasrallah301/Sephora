package com.aventique.sephora.domain.common

sealed class ErrorType {
    object Network : ErrorType()
    object NotFound : ErrorType()
    object Unauthorized : ErrorType()
    data class Unknown(val throwable: Throwable) : ErrorType()
}
fun ErrorType.getErrorMessage(): String = when (this) {
    ErrorType.Network -> "Erreur réseau. Veuillez vérifier votre connexion et réessayer."
    ErrorType.NotFound -> "Produits introuvables."
    ErrorType.Unauthorized -> "Accès non autorisé. Veuillez vous reconnecter."
    is ErrorType.Unknown -> "Une erreur inattendue s'est produite : ${this.throwable.message}"
}