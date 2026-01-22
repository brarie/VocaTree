package com.monovoca.server.user.domain.port

import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.user.domain.model.vo.Email

interface UserQueryRepository {
    fun existsByUsername(username: Username): Boolean
    fun existsByEmail(email: Email): Boolean
}