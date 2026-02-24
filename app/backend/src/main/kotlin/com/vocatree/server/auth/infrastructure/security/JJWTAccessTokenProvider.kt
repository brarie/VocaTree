package com.vocatree.server.auth.infrastructure.security

import com.vocatree.server.auth.domain.exception.InvalidAccessTokenException
import com.vocatree.server.auth.domain.exception.TokenExpiredException
import com.vocatree.server.auth.domain.model.AuthToken
import com.vocatree.server.auth.domain.port.AccessTokenProvider
import com.vocatree.server.common.domain.model.vo.enumerated.UserRole
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JJWTAccessTokenProvider(
    @Value("\${app.jwt.secret-key}") secretKey: String
) : AccessTokenProvider {

    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

    override fun issue(authToken: AuthToken): String {
        return Jwts.builder()
            .signWith(key)
            .subject(authToken.userId)
            .issuedAt(Date(authToken.issuedAt))
            .expiration(Date(authToken.expiresAt))
            .claim("role", authToken.role.name)
            .compact()
    }

    override fun parse(accessToken: String): AuthToken {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .payload
                .let {
                    AuthToken(
                        userId = it.subject,
                        role = UserRole.valueOf(it.get("role", String::class.java)),
                        issuedAt = it.issuedAt.time,
                        expiresAt = it.expiration.time
                    )
                }
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException()
        } catch (e: JwtException) {
            throw InvalidAccessTokenException()
        }
    }
}