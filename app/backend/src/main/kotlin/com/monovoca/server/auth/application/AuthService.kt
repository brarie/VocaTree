package com.monovoca.server.auth.application

import com.monovoca.server.auth.application.dto.TokenResult
import com.monovoca.server.auth.domain.exception.AuthUserNotFoundException
import com.monovoca.server.auth.domain.exception.InvalidPasswordException
import com.monovoca.server.auth.domain.exception.InvalidRefreshTokenException
import com.monovoca.server.auth.domain.port.AuthUserQueryRepository
import com.monovoca.server.auth.domain.port.RefreshTokenRepository
import com.monovoca.server.common.domain.model.vo.Username
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authUserQueryRepository: AuthUserQueryRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenIssuer: TokenIssuer,
) {

    fun login(username: String, rawPassword: String): TokenResult {
        val authUser = authUserQueryRepository.findAuthUserByUsername(Username(username))
            ?: throw AuthUserNotFoundException()
        if (authUser.password == null || !authUser.password.matches(rawPassword, passwordEncoder))
            throw InvalidPasswordException()

        val tokenResult = tokenIssuer.issueTokenResult(authUser)

        refreshTokenRepository.save(tokenResult.refreshToken, authUser.id, tokenResult.refreshExpMillis)

        return tokenResult
    }

    fun logout(userId: String) {
        refreshTokenRepository.deleteByUserId(userId)
    }

    fun refresh(refreshToken: String): TokenResult {
        val userId = refreshTokenRepository.findUserIdByRefreshToken(refreshToken)
            ?: throw InvalidRefreshTokenException()
        val authUser = authUserQueryRepository.findAuthUserById(userId)
            ?: throw AuthUserNotFoundException()

        val tokenResult = tokenIssuer.issueTokenResult(authUser)

        refreshTokenRepository.deleteByUserId(userId)
        refreshTokenRepository.save(tokenResult.refreshToken, authUser.id, tokenResult.refreshExpMillis)

        return tokenResult
    }
}