package com.monovoca.server.user.controller.request

import com.monovoca.server.user.domain.model.vo.EmailPolicy
import com.monovoca.server.user.domain.model.vo.NicknamePolicy
import com.monovoca.server.user.domain.model.vo.RawPasswordPolicy
import com.monovoca.server.user.domain.model.vo.UsernamePolicy
import jakarta.validation.constraints.Pattern

data class SignUpRequest(
    @field:Pattern(regexp = UsernamePolicy.regex, message = UsernamePolicy.message)
    val username: String,
    @field:Pattern(regexp = EmailPolicy.regex, message = EmailPolicy.message)
    val email: String,
    @field:Pattern(regexp = NicknamePolicy.regex, message = NicknamePolicy.message)
    val nickname: String,
    @field:Pattern(regexp = RawPasswordPolicy.regex, message = RawPasswordPolicy.message)
    val rawPassword: String,
)

data class CertificateRequest(
    val code: String,
)

data class ResendCertificationRequest(
    val tempKey: String,
)