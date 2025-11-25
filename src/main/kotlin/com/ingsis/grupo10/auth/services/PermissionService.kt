package com.ingsis.grupo10.auth.services

import com.ingsis.grupo10.auth.models.permission.PermissionType
import com.ingsis.grupo10.auth.models.permission.SnippetPermission
import com.ingsis.grupo10.auth.models.permission.dto.PermissionResponse
import com.ingsis.grupo10.auth.models.permission.dto.SnippetPermissionInfo
import com.ingsis.grupo10.auth.repositories.SnippetPermissionRepository
import com.ingsis.grupo10.auth.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PermissionService(
    private val userRepository: UserRepository,
    private val snippetPermissionRepository: SnippetPermissionRepository,
) {
    // Called by snippet service when a snippet is created
    @Transactional
    fun registerSnippet(
        snippetId: UUID,
        ownerId: String,
    ) {
        // DO NOT auto-create user - user MUST exist
        val owner =
            userRepository
                .findById(ownerId)
                .orElseThrow { IllegalArgumentException("User not found: $ownerId. User must be registered first.") }

        // Check if snippet already has an owner
        val existingOwner =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.permission == PermissionType.OWNER }

        if (existingOwner != null) {
            throw IllegalArgumentException("Snippet already registered: $snippetId")
        }

        // Create owner permission entry
        val ownerPermission =
            SnippetPermission(
                snippetId = snippetId,
                user = owner,
                permission = PermissionType.OWNER,
            )
        snippetPermissionRepository.save(ownerPermission)
    }

    // Called by snippet service when a snippet is deleted
    @Transactional
    fun unregisterSnippet(
        snippetId: UUID,
        requesterId: String,
    ) {
        val ownerPermission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.permission == PermissionType.OWNER }
                ?: throw IllegalArgumentException("Snippet not found: $snippetId")

        if (ownerPermission.user.id != requesterId) {
            throw IllegalAccessException("Only the owner can unregister this snippet")
        }

        // Delete all permissions for this snippet (including owner)
        val allPermissions = snippetPermissionRepository.findBySnippetId(snippetId)
        snippetPermissionRepository.deleteAll(allPermissions)
    }

    // Check if user has specific permission on snippet
    fun hasPermission(
        userId: String,
        snippetId: UUID,
        requiredPermission: PermissionType,
    ): Boolean {
        val userPermission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.user.id == userId }
                ?: return false

        println(
            "User $userId permissions for snippet $snippetId: ${
                snippetPermissionRepository.findBySnippetId(snippetId).map { it.user.id + ":" + it.permission }
            }",
        )

        return when (requiredPermission) {
            PermissionType.READ ->
                userPermission.permission in
                    listOf(
                        PermissionType.READ,
                        PermissionType.WRITE,
                        PermissionType.OWNER,
                    )
            PermissionType.WRITE ->
                userPermission.permission in
                    listOf(
                        PermissionType.WRITE,
                        PermissionType.OWNER,
                    )
            PermissionType.OWNER -> userPermission.permission == PermissionType.OWNER
        }
    }

    // Get user's permission level for a snippet
    fun getUserPermission(
        userId: String,
        snippetId: UUID,
    ): PermissionResponse {
        val permission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
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
            canRead =
                permission.permission in
                    listOf(
                        PermissionType.READ,
                        PermissionType.WRITE,
                        PermissionType.OWNER,
                    ),
            canWrite =
                permission.permission in
                    listOf(
                        PermissionType.WRITE,
                        PermissionType.OWNER,
                    ),
            isOwner = permission.permission == PermissionType.OWNER,
        )
    }

    // Get all snippets a user can access
    fun getUserAccessibleSnippets(userId: String): List<SnippetPermissionInfo> =
        snippetPermissionRepository.findByUserId(userId).map { permission ->
            // Get the owner for this snippet
            val owner =
                snippetPermissionRepository
                    .findBySnippetId(permission.snippetId)
                    .find { it.permission == PermissionType.OWNER }
                    ?.user

            SnippetPermissionInfo(
                snippetId = permission.snippetId,
                ownerId = owner?.id ?: "unknown",
                ownerEmail = owner?.email,
                permission = permission.permission,
            )
        }

    // Grant or update permission
    @Transactional
    fun grantPermission(
        snippetId: UUID,
        targetUserEmail: String
    ) {
        val ownerPermission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.permission == PermissionType.OWNER }
                ?: throw IllegalArgumentException("Snippet not found: $snippetId")

        val targetUser =
            userRepository.findByEmail(targetUserEmail)
                ?: throw IllegalArgumentException("Target user not found: $targetUserEmail")

        // Can't grant permission to owner
        if (targetUser.id == ownerPermission.user.id) {
            throw IllegalArgumentException("Cannot grant permission to the owner")
        }

        // Check if permission already exists
        val existingPermission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.user.id == targetUser.id }

        if (existingPermission != null) {
            snippetPermissionRepository.save(existingPermission)
        } else {
            val newPermission =
                SnippetPermission(
                    snippetId = snippetId,
                    user = targetUser,
                    permission = PermissionType.READ,
                )
            snippetPermissionRepository.save(newPermission)
        }
    }

    // Revoke permission
    @Transactional
    fun revokePermission(
        requesterId: String,
        snippetId: UUID,
        targetUserId: String,
    ) {
        val ownerPermission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.permission == PermissionType.OWNER }
                ?: throw IllegalArgumentException("Snippet not found: $snippetId")

        if (ownerPermission.user.id != requesterId) {
            throw IllegalAccessException("Only the owner can revoke permissions")
        }

        val permission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.user.id == targetUserId }
                ?: throw IllegalArgumentException("Permission not found")

        snippetPermissionRepository.delete(permission)
    }

    // Get all users who have access to a snippet
    fun getSnippetPermissions(
        requesterId: String,
        snippetId: UUID,
    ): List<SnippetPermissionInfo> {
        val ownerPermission =
            snippetPermissionRepository
                .findBySnippetId(snippetId)
                .find { it.permission == PermissionType.OWNER }
                ?: throw IllegalArgumentException("Snippet not found: $snippetId")

        if (ownerPermission.user.id != requesterId) {
            throw IllegalAccessException("Only the owner can view all permissions")
        }

        return snippetPermissionRepository
            .findBySnippetId(snippetId)
            .filter { it.permission != PermissionType.OWNER } // Don't include owner in shared list
            .map {
                SnippetPermissionInfo(
                    snippetId = it.snippetId,
                    ownerId = it.user.id,
                    ownerEmail = it.user.email,
                    permission = it.permission,
                )
            }
    }

    fun getUserOwnedSnippets(userId: String): List<SnippetPermissionInfo> =
        snippetPermissionRepository
            .findByUserId(userId)
            .filter { it.permission == PermissionType.OWNER }
            .map { permission ->
                SnippetPermissionInfo(
                    snippetId = permission.snippetId,
                    ownerId = permission.user.id,
                    ownerEmail = permission.user.email,
                    permission = permission.permission,
                )
            }

    fun getUserReadSnippets(userId: String): List<SnippetPermissionInfo> =
        snippetPermissionRepository
            .findByUserId(userId)
            .filter { it.permission == PermissionType.READ }
            .map { permission ->
                SnippetPermissionInfo(
                    snippetId = permission.snippetId,
                    ownerId = permission.user.id,
                    ownerEmail = permission.user.email,
                    permission = permission.permission,
                )
            }
}
