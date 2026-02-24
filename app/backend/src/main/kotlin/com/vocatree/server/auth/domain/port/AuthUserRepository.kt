package com.vocatree.server.auth.domain.port

import com.vocatree.server.auth.domain.model.AuthUser
import com.vocatree.server.common.domain.model.vo.Username

interface AuthUserRepository {
    fun findAuthUserById(id: String): AuthUser?
    fun findAuthUserByUsername(username: Username): AuthUser?
}