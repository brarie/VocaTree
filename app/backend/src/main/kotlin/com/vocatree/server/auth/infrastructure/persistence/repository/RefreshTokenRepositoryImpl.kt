package com.vocatree.server.auth.infrastructure.persistence.repository

import com.vocatree.server.auth.domain.port.RefreshTokenRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

/**
 * Redis 에 Refresh Token 을 저장하고 조회하는 Repository 구현체
 * auth:refresh-token:$refreshToken = userId
 * auth:userId:$userId              = refreshToken
 */
@Repository
class RefreshTokenRepositoryImpl(
    private val redisTemplate: StringRedisTemplate,
) : RefreshTokenRepository {

    override fun save(refreshToken: String, userId: String, refreshExpMillis: Long) {
        redisTemplate.opsForValue().get("auth:userId:$userId")?.let { existingRefreshToken ->
            redisTemplate.delete("auth:refresh-token:$existingRefreshToken")
        }
        redisTemplate.opsForValue().set("auth:refresh-token:$refreshToken", userId, refreshExpMillis, TimeUnit.MILLISECONDS)
        redisTemplate.opsForValue().set("auth:userId:$userId", refreshToken, refreshExpMillis, TimeUnit.MILLISECONDS)
    }

    override fun findUserIdByRefreshToken(refreshToken: String): String? {
        return redisTemplate.opsForValue().get("auth:refresh-token:$refreshToken")
    }

    override fun deleteByUserId(userId: String) {
        val refreshToken = redisTemplate.opsForValue().get("auth:userId:$userId")
        if (refreshToken != null) {
            redisTemplate.delete("auth:refresh-token:$refreshToken")
            redisTemplate.delete("auth:userId:$userId")
        }
    }
}