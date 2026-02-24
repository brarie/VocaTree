package com.vocatree.server.user.infrastructure.web

import com.vocatree.server.user.application.UserRegisterService
import com.vocatree.server.user.infrastructure.web.request.CertificateRequest
import com.vocatree.server.user.infrastructure.web.request.ResendCertificationRequest
import com.vocatree.server.user.infrastructure.web.request.SignUpRequest
import com.vocatree.server.user.infrastructure.web.response.SignUpResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserRegisterController(
    private val userRegisterService: UserRegisterService
) {

    @PostMapping("/sign-up")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<SignUpResponse> {
        val tempKey = userRegisterService.signUp(
            usernameStr = request.username,
            emailStr = request.email,
            nicknameStr = request.nickname,
            rawPassword = request.rawPassword
        )
        return ResponseEntity.ok(SignUpResponse(tempKey))
    }

    @PostMapping("/verify")
    fun certificate(@RequestBody request: CertificateRequest): ResponseEntity<Unit> {
        userRegisterService.certificate(request.code)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/resend-verification")
    fun resendCertification(@RequestBody request: ResendCertificationRequest): ResponseEntity<Unit> {
        userRegisterService.resendCertification(request.tempKey)
        return ResponseEntity.ok().build()
    }
}