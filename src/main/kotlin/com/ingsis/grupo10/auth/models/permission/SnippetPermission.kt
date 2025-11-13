package com.ingsis.grupo10.auth.models.permission

import com.ingsis.grupo10.auth.models.user.User
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "snippet_permissions")
class SnippetPermission(
    @Id
    val id: UUID = UUID.randomUUID(),
    val snippetId: UUID, // Just store the snippet ID directly
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,
    @Enumerated(EnumType.STRING)
    var permission: PermissionType,
) {
    constructor() : this(UUID.randomUUID(), UUID.randomUUID(), User(), PermissionType.READ)
}
