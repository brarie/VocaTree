package com.monovoca.server.user.domain.repository

import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Username

interface UserQueryRepository {
    fun existsByUsername(username: Username): Boolean
    fun existsByEmail(email: Email): Boolean
}