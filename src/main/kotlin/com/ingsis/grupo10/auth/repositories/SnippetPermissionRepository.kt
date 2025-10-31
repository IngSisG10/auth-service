package com.ingsis.grupo10.auth.repositories

import com.ingsis.grupo10.auth.entities.SnippetPermission
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SnippetPermissionRepository : JpaRepository<SnippetPermission, UUID> {
    fun findByUserId(userId: String): List<SnippetPermission>

    fun findBySnippetSnippetId(snippetId: UUID): List<SnippetPermission>

    fun existsByUserIdAndSnippetSnippetId(
        userId: String,
        snippetId: UUID,
    ): Boolean
}
