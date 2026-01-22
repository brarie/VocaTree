package com.monovoca.server.auth.application

import com.monovoca.server.auth.application.dto.TokenResult
import com.monovoca.server.auth.domain.model.AuthToken
import com.monovoca.server.auth.domain.model.AuthUser
import com.monovoca.server.auth.domain.port.AccessTokenProvider
import com.monovoca.server.common.domain.port.DateHolder
import com.monovoca.server.common.domain.port.UUIDHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TokenIssuer(
    private val uuidHolder: UUIDHolder,
    private val dateHolder: DateHolder,
    @param:Value("\${app.jwt.access-token-expiration-ms}") private val accessExpMillis: Long,
    @param:Value("\${app.jwt.refresh-token-expiration-ms}") private val refreshExpMillis: Long,
    private val accessTokenProvider: AccessTokenProvider,
) {

    fun issueTokenResult(authUser: AuthUser): TokenResult {
        val issuedAt = dateHolder.now()
        val expiresAt = issuedAt + accessExpMillis
        val authToken = AuthToken.of(authUser, issuedAt, expiresAt)

        val accessToken = accessTokenProvider.issue(authToken)
        val refreshToken = uuidHolder.generateUUID()

        return TokenResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessExpMillis = accessExpMillis,
            refreshExpMillis = refreshExpMillis
        )
    }
}