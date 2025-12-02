package com.ingsis.grupo10.auth.controllers

import com.ingsis.grupo10.auth.models.permission.PermissionType
import com.ingsis.grupo10.auth.models.permission.dto.GrantPermissionRequest
import com.ingsis.grupo10.auth.models.permission.dto.PermissionResponse
import com.ingsis.grupo10.auth.models.permission.dto.SnippetPermissionInfo
import com.ingsis.grupo10.auth.models.snippet.dto.RegisterSnippetRequest
import com.ingsis.grupo10.auth.services.PermissionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/permissions")
class PermissionController(
    private val permissionService: PermissionService,
) {
    // Called by snippet service when creating a snippet
    @PostMapping("/snippets")
    fun registerSnippet(
        @RequestBody request: RegisterSnippetRequest,
    ): ResponseEntity<Void> {
        permissionService.registerSnippet(request.snippetId, request.ownerId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    // Called by snippet service when deleting a snippet
    @DeleteMapping("/snippets/{snippetId}")
    fun unregisterSnippet(
        @PathVariable snippetId: UUID,
        @RequestParam userId: String,
    ): ResponseEntity<Void> {
        permissionService.unregisterSnippet(snippetId, userId)
        return ResponseEntity.noContent().build()
    }

    // Check if user has permission (for snippet service to validate access)
    @GetMapping("/snippets/{snippetId}/check")
    fun checkPermission(
        @PathVariable snippetId: UUID,
        @RequestParam userId: String,
        @RequestParam(required = false, defaultValue = "READ") requiredPermission: PermissionType,
    ): ResponseEntity<Map<String, Boolean>> {
        val hasPermission = permissionService.hasPermission(userId, snippetId, requiredPermission)
        println("Checking permission for user=$userId on snippet=$snippetId with requiredPermission=$requiredPermission")
        return ResponseEntity.ok(mapOf("hasPermission" to hasPermission))
    }

    // Get detailed permission info for a user on a snippet
    @GetMapping("/snippets/{snippetId}")
    fun getUserPermission(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: UUID,
    ): ResponseEntity<PermissionResponse> {
        val userId = jwt.subject
        val permission = permissionService.getUserPermission(userId, snippetId)
        return ResponseEntity.ok(permission)
    }

    // Get all snippets accessible by the current user
    @GetMapping("/my-snippets")
    fun getMyAccessibleSnippets(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<SnippetPermissionInfo>> {
        val userId = jwt.subject
        val snippets = permissionService.getUserAccessibleSnippets(userId)
        return ResponseEntity.ok(snippets)
    }

    // Grant permission to another user (only owner can do this)
    @PostMapping("/snippets/grant")
    fun grantPermission(
        @RequestBody request: GrantPermissionRequest,
    ): ResponseEntity<Void> {
        permissionService.grantPermission(request.snippetId, request.targetUserEmail)
        return ResponseEntity.ok().build()
    }

    // Revoke permission from a user
    @DeleteMapping("/snippets/{snippetId}/users/{targetUserId}")
    fun revokePermission(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: UUID,
        @PathVariable targetUserId: String,
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        permissionService.revokePermission(userId, snippetId, targetUserId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/snippets/{snippetId}/users")
    fun getSnippetPermissions(
        @PathVariable snippetId: UUID,
        @RequestParam userId: String,
    ): ResponseEntity<List<SnippetPermissionInfo>> {
        val permissions = permissionService.getSnippetPermissions(userId, snippetId)
        return ResponseEntity.ok(permissions)
    }

    @GetMapping("/owned-snippets")
    fun getOwnedSnippets(
        @RequestParam userId: String,
    ): ResponseEntity<List<SnippetPermissionInfo>> {
        val snippets = permissionService.getUserOwnedSnippets(userId)
        return ResponseEntity.ok(snippets)
    }

    @GetMapping("/accessible-snippets")
    fun getAccessibleSnippets(
        @RequestParam userId: String,
    ): ResponseEntity<List<SnippetPermissionInfo>> {
        val snippets = permissionService.getUserAccessibleSnippets(userId)
        return ResponseEntity.ok(snippets)
    }

    @GetMapping("/read-snippets")
    fun getReadSnippets(
        @RequestParam userId: String,
    ): ResponseEntity<List<SnippetPermissionInfo>> {
        val snippets = permissionService.getUserReadSnippets(userId)
        return ResponseEntity.ok(snippets)
    }

    @GetMapping("/snippets/{snippetId}/owner")
    fun getSnippetOwner(
        @PathVariable snippetId: UUID,
    ): ResponseEntity<SnippetPermissionInfo> {
        val owner =
            permissionService.getSnippetOwner(snippetId)
                ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(owner)
    }
}
