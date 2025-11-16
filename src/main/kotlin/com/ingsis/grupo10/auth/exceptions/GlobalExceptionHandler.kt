package com.ingsis.grupo10.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val error =
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Bad Request",
                message = ex.message ?: "Invalid request",
            )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccess(ex: IllegalAccessException): ResponseEntity<ErrorResponse> {
        val error =
            ErrorResponse(
                status = HttpStatus.FORBIDDEN.value(),
                error = "Forbidden",
                message = ex.message ?: "Access denied",
            )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        val error =
            ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = "Not Found",
                message = ex.message ?: "Resource not found",
            )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val error =
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Internal Server Error",
                message = "An unexpected error occurred",
            )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
