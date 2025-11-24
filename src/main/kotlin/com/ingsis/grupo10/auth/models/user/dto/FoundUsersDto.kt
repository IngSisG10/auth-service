package com.ingsis.grupo10.auth.models.user.dto

data class FoundUsersDto(
    val totalCount: Int,
    val users: List<UIUserResponse>,
)
