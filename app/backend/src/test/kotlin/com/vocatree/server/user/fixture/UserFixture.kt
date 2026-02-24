package com.vocatree.server.user.fixture

import com.vocatree.server.common.domain.model.vo.EncryptedPassword
import com.vocatree.server.common.domain.model.vo.Username
import com.vocatree.server.user.domain.model.User
import com.vocatree.server.user.domain.model.vo.Email
import com.vocatree.server.user.domain.model.vo.Nickname

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
