package com.monovoca.server.user.domain.port

import com.monovoca.server.user.domain.model.User

interface UserCommandRepository {
    fun save(user: User): User
}