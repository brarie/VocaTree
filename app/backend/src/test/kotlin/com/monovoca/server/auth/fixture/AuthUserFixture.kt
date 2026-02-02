package com.monovoca.server.auth.fixture

import com.monovoca.server.auth.domain.model.AuthUser
import com.monovoca.server.common.domain.model.vo.EncryptedPassword
import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.common.domain.model.vo.enumerated.UserRole

object AuthUserFixture {

    fun create(
        id: String = "550e8400-e29b-41d4-a716-446655440000",
        role: UserRole = UserRole.USER,
        username: String = "testuser",
        password: String? = "encryptedpassword"
    ): AuthUser {
        return AuthUser(
            id = id,
            role = role,
            username = Username(username),
            password = password?.let { EncryptedPassword.fromHash(it) }
        )
    }
}
