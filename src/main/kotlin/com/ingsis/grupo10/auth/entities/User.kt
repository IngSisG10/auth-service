package com.ingsis.grupo10.auth.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    val userId: UUID = UUID.randomUUID(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    val permissions: MutableSet<SnippetPermission> = HashSet(),
)
