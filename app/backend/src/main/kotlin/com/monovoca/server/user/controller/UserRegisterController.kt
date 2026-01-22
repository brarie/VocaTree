package com.monovoca.server.user.controller

import com.monovoca.server.user.controller.request.CertificateRequest
import com.monovoca.server.user.controller.request.ResendCertificationRequest
import com.monovoca.server.user.controller.request.SignUpRequest
import com.monovoca.server.user.controller.response.SignUpResponse
import com.monovoca.server.user.service.UserRegisterService
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