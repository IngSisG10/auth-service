package com.ingsis.grupo10.auth.models

data class ValidateTokenResponse(
    val userId: String,
    val email: String?,
    val name: String?,
)
