package com.monovoca.server.user.infrastructure.web.response

import com.monovoca.server.user.domain.model.User

data class UserInfo(
    val id: String,
    val username: String,
    val email: String,
    val nickname: String,
    val role: String
) {
    companion object {
        fun from(user: User) = UserInfo(
            id = user.id!!,
            username = user.username.value,
            email = user.email.value,
            nickname = user.nickname.value,
            role = user.role.name
        )
    }
}