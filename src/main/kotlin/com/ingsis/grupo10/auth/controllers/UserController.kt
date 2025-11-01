package com.ingsis.grupo10.auth.controllers

import com.ingsis.grupo10.auth.models.user.dto.UserResponse
import com.ingsis.grupo10.auth.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UserResponse> {
        val userId = jwt.subject
        val email = jwt.getClaim<String>("email")
        val name = jwt.getClaim<String>("name")

        val user = userService.getOrCreateUser(userId, email, name)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/search")
    fun searchUsers(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam query: String,
    ): ResponseEntity<List<UserResponse>> {
        val users = userService.searchUsers(query)
        return ResponseEntity.ok(users)
    }
}
