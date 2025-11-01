package com.ingsis.grupo10.auth.models.permission.dto

import com.ingsis.grupo10.auth.models.permission.PermissionType

data class GrantPermissionRequest(
    val targetUserEmail: String,
    val permission: PermissionType,
)
