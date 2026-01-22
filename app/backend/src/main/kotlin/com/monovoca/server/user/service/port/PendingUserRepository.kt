package com.monovoca.server.user.service.port

import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.common.domain.vo.Username

interface PendingUserRepository {

    fun save(tempKey: String, user: User, code: String)
    fun existsByUsername(username: Username): Boolean
    fun existsByEmail(email: Email): Boolean
    fun findByTempKey(tempKey: String): User?
    fun findByCode(code: String): User?
    fun updateCode(tempKey: String, newCode: String)
    fun deleteAllByCode(code: String)
}