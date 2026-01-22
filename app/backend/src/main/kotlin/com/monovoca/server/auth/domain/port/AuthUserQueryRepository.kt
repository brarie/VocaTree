package com.monovoca.server.auth.domain.port

import com.monovoca.server.auth.domain.model.AuthUser
import com.monovoca.server.common.domain.model.vo.Username

interface AuthUserQueryRepository {
    fun findAuthUserById(id: String): AuthUser?
    fun findAuthUserByUsername(username: Username): AuthUser?
}