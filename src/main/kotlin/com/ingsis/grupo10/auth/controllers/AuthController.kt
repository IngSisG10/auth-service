package com.ingsis.grupo10.auth.controllers

import com.ingsis.grupo10.auth.models.ValidateTokenResponse
import com.ingsis.grupo10.auth.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
) {
    /**
     * Validate token and return user info
     * Called by Snippet Service to validate tokens
     */
    @GetMapping("/validate")
    fun validateToken(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<ValidateTokenResponse> {
        val userId = jwt.subject
        val namespace = "https://your-app.com"

        val email = jwt.getClaim<String>("$namespace/email")
        val name = jwt.getClaim<String>("$namespace/name")

        // Auto-create user if doesn't exist
        userService.getOrCreateUser(userId, email, name)

        return ResponseEntity.ok(
            ValidateTokenResponse(
                userId = userId,
                email = email,
                name = name,
            ),
        )
    }
}
