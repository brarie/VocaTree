package com.monovoca.server.auth.domain.model

import com.monovoca.server.common.domain.vo.UserRole

/**
 * JWT 랑 1:1 매핑되는 인증 토큰 모델
 */
data class AuthToken(
    val userId: String,
    val role: UserRole,
    val issuedAt: Long,
    val expiresAt: Long,
) {

    init {
        require(expiresAt > issuedAt) { "expiresAt must be greater than issuedAt" }
    }

    companion object {
        fun of(authUser: AuthUser, issuedAt: Long, expiresAt: Long): AuthToken {
            return AuthToken(
                userId = authUser.id,
                role = authUser.role,
                issuedAt = issuedAt,
                expiresAt = expiresAt,
            )
        }
    }
}