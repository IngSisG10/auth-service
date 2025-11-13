package com.ingsis.grupo10.auth.repositories

import com.ingsis.grupo10.auth.models.permission.SnippetPermission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SnippetPermissionRepository : JpaRepository<SnippetPermission, UUID> {
    fun findBySnippetId(snippetId: UUID): List<SnippetPermission>

    fun findByUserId(userId: String): List<SnippetPermission>
}
