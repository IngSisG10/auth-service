package com.ingsis.grupo10.auth.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    val id: String, // Auth0 "sub" claim (e.g., "auth0|65fb2cd13f1234567890abcd")
    var email: String? = null,
    var name: String? = null,
    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], orphanRemoval = true)
    val ownedSnippets: MutableSet<Snippet> = HashSet(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val permissions: MutableSet<SnippetPermission> = HashSet(),
) {
    constructor() : this(id = "", email = null, name = null)
}
