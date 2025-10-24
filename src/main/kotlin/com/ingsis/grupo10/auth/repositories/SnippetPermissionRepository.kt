package com.ingsis.grupo10.auth.repositories

import com.ingsis.grupo10.auth.entities.SnippetPermission
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SnippetPermissionRepository : JpaRepository<SnippetPermission, UUID>
