package com.ingsis.grupo10.auth.controllers

import com.ingsis.grupo10.auth.models.user.dto.FoundUsersDto
import com.ingsis.grupo10.auth.models.user.dto.UserResponse
import com.ingsis.grupo10.auth.repositories.UserRepository
import com.ingsis.grupo10.auth.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val userRepository: UserRepository,
) {
    @PostMapping("/exists")
    fun checkUserExists(
        @RequestBody request: Map<String, String>,
    ): ResponseEntity<Map<String, Boolean>> {
        val userId =
            request["userId"] ?: return ResponseEntity
                .badRequest()
                .body(mapOf("exists" to false))

        val exists = userRepository.existsById(userId)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UserResponse> {
        val userId = jwt.subject
        val namespace = "https://your-app.com"
        val email = jwt.getClaim<String>("$namespace/email")
        val name = jwt.getClaim<String>("$namespace/name")

        // Creates user if doesn't exist (first time user accesses system)
        val user = userService.getOrCreateUser(userId, email, name)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/search")
    fun searchUsers(
        @RequestParam userId: String,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): ResponseEntity<FoundUsersDto> {
        val result = userService.searchUsersWithPagination(userId, email, page, pageSize)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/register-or-login")
    fun registerOrLogin(
        @RequestBody request: Map<String, String?>,
    ): ResponseEntity<UserResponse> {
        val userId =
            request["userId"]
                ?: return ResponseEntity.badRequest().build()
        val email = request["email"]
        val name = request["name"]

        val user = userService.getOrCreateUser(userId, email, name)

        return ResponseEntity.ok(user)
    }
}
