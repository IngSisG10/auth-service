package com.ingsis.grupo10.auth.entities

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
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,
    @ManyToOne
    @JoinColumn(name = "snippet_id")
    var snippet: Snippet,
    @Enumerated(EnumType.STRING)
    var permission: PermissionType,
) {
    constructor() : this(UUID.randomUUID(), User(), Snippet(), PermissionType.READ)
}
