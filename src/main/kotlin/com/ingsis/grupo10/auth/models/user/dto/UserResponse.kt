package com.ingsis.grupo10.auth.models.user.dto

data class UserResponse(
    val userId: String,
    val email: String?,
    val name: String?,
)
