package com.ingsis.grupo10.auth.repositories

import com.ingsis.grupo10.auth.models.snippet.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SnippetRepository : JpaRepository<Snippet, UUID> {
    fun findByOwnerId(ownerId: String): List<Snippet>

    fun existsByOwnerIdAndSnippetId(
        ownerId: String,
        snippetId: UUID,
    ): Boolean
}
