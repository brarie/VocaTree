package com.vocatree.server.auth.application

import com.vocatree.server.auth.application.dto.TokenResult
import com.vocatree.server.auth.domain.exception.AuthUserNotFoundException
import com.vocatree.server.auth.domain.exception.InvalidPasswordException
import com.vocatree.server.auth.domain.exception.InvalidRefreshTokenException
import com.vocatree.server.auth.domain.port.AuthUserRepository
import com.vocatree.server.auth.domain.port.RefreshTokenRepository
import com.vocatree.server.common.domain.model.vo.Username
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenIssuer: TokenIssuer,
) {

    @Transactional
    fun login(username: String, rawPassword: String): TokenResult {
        val authUser = authUserRepository.findAuthUserByUsername(Username(username))
            ?: throw AuthUserNotFoundException()
        if (authUser.password == null || !authUser.password.matches(rawPassword, passwordEncoder))
            throw InvalidPasswordException()

        val tokenResult = tokenIssuer.issueTokenResult(authUser)

        refreshTokenRepository.save(tokenResult.refreshToken, authUser.id, tokenResult.refreshExpMillis)

        return tokenResult
    }

    @Transactional
    fun logout(userId: String) {
        refreshTokenRepository.deleteByUserId(userId)
    }

    @Transactional
    fun refresh(refreshToken: String): TokenResult {
        val userId = refreshTokenRepository.findUserIdByRefreshToken(refreshToken)
            ?: throw InvalidRefreshTokenException()
        val authUser = authUserRepository.findAuthUserById(userId)
            ?: throw AuthUserNotFoundException()

        val tokenResult = tokenIssuer.issueTokenResult(authUser)

        refreshTokenRepository.deleteByUserId(userId)
        refreshTokenRepository.save(tokenResult.refreshToken, authUser.id, tokenResult.refreshExpMillis)

        return tokenResult
    }
}