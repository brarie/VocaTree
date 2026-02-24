package com.vocatree.server.user.domain.model

import com.vocatree.server.common.domain.model.vo.EncryptedPassword
import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.common.domain.model.vo.enumerated.UserRole
import com.vocatree.server.user.domain.model.vo.Email
import com.vocatree.server.user.domain.model.vo.Nickname

data class User(
    val id: String? = null,
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