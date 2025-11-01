package com.ingsis.grupo10.auth.services

import com.ingsis.grupo10.auth.models.permission.PermissionType
import com.ingsis.grupo10.auth.models.permission.SnippetPermission
import com.ingsis.grupo10.auth.models.permission.dto.PermissionResponse
import com.ingsis.grupo10.auth.models.permission.dto.SnippetPermissionInfo
import com.ingsis.grupo10.auth.models.snippet.Snippet
import com.ingsis.grupo10.auth.repositories.SnippetPermissionRepository
import com.ingsis.grupo10.auth.repositories.SnippetRepository
import com.ingsis.grupo10.auth.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PermissionService(
    private val snippetRepository: SnippetRepository,
    private val userRepository: UserRepository,
    private val snippetPermissionRepository: SnippetPermissionRepository,
) {
    // Called by snippet service when a snippet is created
    @Transactional
    fun registerSnippet(
        snippetId: UUID,
        ownerId: String,
    ) {
        val owner =
            userRepository
                .findById(ownerId)
                .orElseThrow { IllegalArgumentException("User not found: $ownerId") }

        if (snippetRepository.existsById(snippetId)) {
            throw IllegalArgumentException("Snippet already registered: $snippetId")
        }

        val snippet =
            Snippet(
                snippetId = snippetId,
                owner = owner,
            )
        snippetRepository.save(snippet)
    }

    // Called by snippet service when a snippet is deleted
    @Transactional
    fun unregisterSnippet(
        snippetId: UUID,
        requesterId: String,
    ) {
        val snippet =
            snippetRepository
                .findById(snippetId)
                .orElseThrow { IllegalArgumentException("Snippet not found: $snippetId") }

        if (snippet.owner.id != requesterId) {
            throw IllegalAccessException("Only the owner can unregister this snippet")
        }

        snippetRepository.delete(snippet)
    }

    // Check if user has specific permission on snippet
    fun hasPermission(
        userId: String,
        snippetId: UUID,
        requiredPermission: PermissionType,
    ): Boolean {
        val snippet = snippetRepository.findById(snippetId).orElse(null) ?: return false

        // Owner has all permissions
        if (snippet.owner.id == userId) {
            return true
        }

        // Check explicit permissions
        val permission =
            snippetPermissionRepository
                .findBySnippetSnippetId(snippetId)
                .find { it.user.id == userId }
                ?: return false

        return when (requiredPermission) {
            PermissionType.READ -> permission.permission in listOf(PermissionType.READ, PermissionType.WRITE, PermissionType.OWNER)
            PermissionType.WRITE -> permission.permission in listOf(PermissionType.WRITE, PermissionType.OWNER)
            PermissionType.OWNER -> permission.permission == PermissionType.OWNER
        }
    }

    // Get user's permission level for a snippet
    fun getUserPermission(
        userId: String,
        snippetId: UUID,
    ): PermissionResponse {
        val snippet =
            snippetRepository
                .findById(snippetId)
                .orElseThrow { IllegalArgumentException("Snippet not found: $snippetId") }

        if (snippet.owner.id == userId) {
            return PermissionResponse(
                snippetId = snippetId,
                userId = userId,
                permission = PermissionType.OWNER,
                canRead = true,
                canWrite = true,
                isOwner = true,
            )
        }

        val permission =
            snippetPermissionRepository
                .findBySnippetSnippetId(snippetId)
                .find { it.user.id == userId }

        if (permission == null) {
            return PermissionResponse(
                snippetId = snippetId,
                userId = userId,
                permission = null,
                canRead = false,
                canWrite = false,
                isOwner = false,
            )
        }

        return PermissionResponse(
            snippetId = snippetId,
            userId = userId,
            permission = permission.permission,
            canRead = permission.permission in listOf(PermissionType.READ, PermissionType.WRITE, PermissionType.OWNER),
            canWrite = permission.permission in listOf(PermissionType.WRITE, PermissionType.OWNER),
            isOwner = permission.permission == PermissionType.OWNER,
        )
    }

    // Get all snippets a user can access
    fun getUserAccessibleSnippets(userId: String): List<SnippetPermissionInfo> {
        val ownedSnippets =
            snippetRepository.findByOwnerId(userId).map {
                SnippetPermissionInfo(
                    snippetId = it.snippetId,
                    ownerId = it.owner.id,
                    ownerEmail = it.owner.email,
                    permission = PermissionType.OWNER,
                )
            }

        val sharedSnippets =
            snippetPermissionRepository.findByUserId(userId).map {
                SnippetPermissionInfo(
                    snippetId = it.snippet.snippetId,
                    ownerId = it.snippet.owner.id,
                    ownerEmail = it.snippet.owner.email,
                    permission = it.permission,
                )
            }

        return ownedSnippets + sharedSnippets
    }

    // Grant or update permission
    @Transactional
    fun grantPermission(
        requesterId: String,
        snippetId: UUID,
        targetUserEmail: String,
        permission: PermissionType,
    ) {
        val snippet =
            snippetRepository
                .findById(snippetId)
                .orElseThrow { IllegalArgumentException("Snippet not found: $snippetId") }

        // Only owner can grant permissions
        if (snippet.owner.id != requesterId) {
            throw IllegalAccessException("Only the owner can grant permissions")
        }

        val targetUser =
            userRepository.findByEmail(targetUserEmail)
                ?: throw IllegalArgumentException("Target user not found: $targetUserEmail")

        // Can't grant permission to owner
        if (targetUser.id == snippet.owner.id) {
            throw IllegalArgumentException("Cannot grant permission to the owner")
        }

        // Check if permission already exists
        val existingPermission =
            snippetPermissionRepository
                .findBySnippetSnippetId(snippetId)
                .find { it.user.id == targetUser.id }

        if (existingPermission != null) {
            existingPermission.permission = permission
            snippetPermissionRepository.save(existingPermission)
        } else {
            val snippetPermission =
                SnippetPermission(
                    user = targetUser,
                    snippet = snippet,
                    permission = permission,
                )
            snippetPermissionRepository.save(snippetPermission)
        }
    }

    // Revoke permission
    @Transactional
    fun revokePermission(
        requesterId: String,
        snippetId: UUID,
        targetUserId: String,
    ) {
        val snippet =
            snippetRepository
                .findById(snippetId)
                .orElseThrow { IllegalArgumentException("Snippet not found: $snippetId") }

        // Only owner can revoke permissions
        if (snippet.owner.id != requesterId) {
            throw IllegalAccessException("Only the owner can revoke permissions")
        }

        val permission =
            snippetPermissionRepository
                .findBySnippetSnippetId(snippetId)
                .find { it.user.id == targetUserId }
                ?: throw IllegalArgumentException("Permission not found")

        snippetPermissionRepository.delete(permission)
    }

    // Get all users who have access to a snippet
    fun getSnippetPermissions(
        requesterId: String,
        snippetId: UUID,
    ): List<SnippetPermissionInfo> {
        val snippet =
            snippetRepository
                .findById(snippetId)
                .orElseThrow { IllegalArgumentException("Snippet not found: $snippetId") }

        // Only owner can view all permissions
        if (snippet.owner.id != requesterId) {
            throw IllegalAccessException("Only the owner can view all permissions")
        }

        return snippetPermissionRepository.findBySnippetSnippetId(snippetId).map {
            SnippetPermissionInfo(
                snippetId = it.snippet.snippetId,
                ownerId = it.user.id,
                ownerEmail = it.user.email,
                permission = it.permission,
            )
        }
    }
}
