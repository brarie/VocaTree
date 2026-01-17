package com.monovoca.server.user.domain.repository

import com.monovoca.server.user.domain.model.User

interface UserCommandRepository {
    fun save(user: User): User
}