package com.ingsis.grupo10.auth.models.permission.dto

import com.ingsis.grupo10.auth.models.permission.PermissionType
import java.util.UUID

data class PermissionResponse(
    val snippetId: UUID,
    val userId: String,
    val permission: PermissionType?,
    val canRead: Boolean,
    val canWrite: Boolean,
    val isOwner: Boolean,
)
