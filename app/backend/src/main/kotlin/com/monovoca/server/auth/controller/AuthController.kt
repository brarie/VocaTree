package com.monovoca.server.auth.controller

import com.monovoca.server.auth.controller.request.LoginRequest
import com.monovoca.server.auth.controller.response.LoginResponse
import com.monovoca.server.auth.infrastructure.UserPrincipal
import com.monovoca.server.auth.service.AuthService
import com.monovoca.server.auth.service.dto.TokenResult
import io.swagger.v3.oas.annotations.Parameter
import jakarta.servlet.http.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val tokenResult = authService.login(loginRequest)
        return tokenResultToResponseEntity(tokenResult)
    }

    /**
     * FrontEnd에서 access-token 삭제하도록 처리
     */
    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<Unit> {
        authService.logout(userPrincipal.id)
        val cookie = ResponseCookie.from("refresh-token")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }

    @PostMapping("/refresh")
    fun refresh(@CookieValue("refresh-token") @Parameter(hidden = true) refreshCookie: Cookie): ResponseEntity<LoginResponse> {
        val tokenResult = authService.refresh(refreshCookie.value)
        return tokenResultToResponseEntity(tokenResult)
    }

    private fun tokenResultToResponseEntity(tokenResult: TokenResult): ResponseEntity<LoginResponse> {
        val cookie = ResponseCookie.from("refresh-token", tokenResult.refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge((tokenResult.refreshExpMillis / 1000))
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(
                LoginResponse(
                    accessToken = tokenResult.accessToken,
                    accessExpSec = (tokenResult.accessExpMillis / 1000).toInt()
                )
            )
    }
}