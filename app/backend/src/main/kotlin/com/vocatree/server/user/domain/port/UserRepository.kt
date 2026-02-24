package com.vocatree.server.user.domain.port

import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.model.vo.Email

interface UserRepository {
    fun save(user: User): User
    fun findById(id: String): User?
    fun existsByUsername(username: Username): Boolean
    fun existsByEmail(email: Email): Boolean
}