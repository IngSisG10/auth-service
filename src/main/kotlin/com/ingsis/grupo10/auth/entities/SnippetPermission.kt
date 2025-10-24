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
data class SnippetPermission(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
    @ManyToOne
    @JoinColumn(name = "snippet_id")
    val snippet: Snippet,
    @Enumerated(EnumType.STRING)
    val permission: PermissionType,
)
