package com.monovoca.server.auth.domain.model

import com.monovoca.server.common.domain.model.vo.EncryptedPassword
import com.monovoca.server.common.domain.model.vo.UserRole
import com.monovoca.server.common.domain.model.vo.Username

/**
 * 인증 관련 사용자 정보 모델
 * password는 login의 경우에만 존재하며, 그 외의 경우에는 null
 */
data class AuthUser(
    val id: String,
    val role: UserRole,
    val username: Username,
    val password: EncryptedPassword?,
)