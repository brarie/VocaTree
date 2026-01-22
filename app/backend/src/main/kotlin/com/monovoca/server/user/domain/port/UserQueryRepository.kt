package com.monovoca.server.user.domain.port

import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.common.domain.vo.Username

interface UserQueryRepository {
    fun existsByUsername(username: Username): Boolean
    fun existsByEmail(email: Email): Boolean
}