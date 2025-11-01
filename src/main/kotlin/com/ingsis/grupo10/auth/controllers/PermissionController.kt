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
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: UUID,
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        permissionService.unregisterSnippet(snippetId, userId)
        return ResponseEntity.noContent().build()
    }

    // Check if user has permission (for snippet service to validate access)
    @GetMapping("/snippets/{snippetId}/check")
    fun checkPermission(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: UUID,
        @RequestParam(required = false, defaultValue = "READ") requiredPermission: PermissionType,
    ): ResponseEntity<Map<String, Boolean>> {
        val userId = jwt.subject
        val hasPermission = permissionService.hasPermission(userId, snippetId, requiredPermission)
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
    @PostMapping("/snippets/{snippetId}/grant")
    fun grantPermission(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: UUID,
        @RequestBody request: GrantPermissionRequest,
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        permissionService.grantPermission(userId, snippetId, request.targetUserEmail, request.permission)
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

    // Get all permissions for a snippet (only owner can view)
    @GetMapping("/snippets/{snippetId}/users")
    fun getSnippetPermissions(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable snippetId: UUID,
    ): ResponseEntity<List<SnippetPermissionInfo>> {
        val userId = jwt.subject
        val permissions = permissionService.getSnippetPermissions(userId, snippetId)
        return ResponseEntity.ok(permissions)
    }
}
