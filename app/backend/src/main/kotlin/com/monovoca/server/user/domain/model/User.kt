package com.monovoca.server.user.domain.model

import com.monovoca.server.user.domain.model.vo.*

data class User(
    val id: Long? = null,
    val username: Username,
    val email: Email,
    val nickname: Nickname,
    val password: EncryptedPassword,
    val role: UserRole = UserRole.USER,
) {
    companion object {
        fun create(
            username: Username,
            email: Email,
            nickname: Nickname,
            password: EncryptedPassword,
        ): User {
            return User(
                username = username, email = email,
                nickname = nickname, password = password,
            )
        }
    }

    fun updateNickname(toNickname: Nickname): User {
        return this.copy(nickname = toNickname)
    }
}