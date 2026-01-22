package com.monovoca.server.user.fixture

import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.common.domain.vo.EncryptedPassword
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.common.domain.vo.Username

object UserFixture {

    fun create(
        id: String? = null,
        username: String = "testuser",
        email: String = "test@example.com",
        nickname: String = "테스트유저",
        password: String = "encryptedpassword"
    ): User {
        return User(
            id = id,
            username = Username(username),
            email = Email(email),
            nickname = Nickname(nickname),
            password = EncryptedPassword.fromHash(password)
        )
    }
}
