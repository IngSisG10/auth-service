package com.ingsis.grupo10.auth.models.permission.dto

import com.ingsis.grupo10.auth.models.permission.PermissionType
import java.util.UUID

data class SnippetPermissionInfo(
    val snippetId: UUID,
    val ownerId: String?,
    val ownerEmail: String?,
    val ownerName: String?,
    val permission: PermissionType,
)
