package com.ingsis.grupo10.auth.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "snippets")
class Snippet(
    @Id
    val snippetId: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "owner_id")
    var owner: User,
    @OneToMany(mappedBy = "snippet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val permissions: MutableSet<SnippetPermission> = HashSet(),
) {
    constructor() : this(UUID.randomUUID(), User())
}
