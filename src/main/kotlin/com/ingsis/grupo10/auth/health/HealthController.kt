package com.ingsis.grupo10.auth.health

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/")
    fun getHealth(): ResponseEntity<String> = ResponseEntity.ok().body("I'm alive!")

    @GetMapping("/secret/health")
    fun getToken(): ResponseEntity<String> = ResponseEntity.ok().body("you are logged and alive!")
}
