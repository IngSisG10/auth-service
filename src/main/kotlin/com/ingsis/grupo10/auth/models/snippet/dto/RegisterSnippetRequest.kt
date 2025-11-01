package com.ingsis.grupo10.auth.models.snippet.dto

import java.util.UUID

data class RegisterSnippetRequest(
    val snippetId: UUID,
    val ownerId: String,
)
