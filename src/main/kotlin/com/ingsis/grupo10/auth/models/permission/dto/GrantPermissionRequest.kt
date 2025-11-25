package com.ingsis.grupo10.auth.models.permission.dto

import com.ingsis.grupo10.auth.models.permission.PermissionType
import java.util.UUID

data class GrantPermissionRequest(
    val snippetId: UUID,
    val targetUserEmail: String
)
