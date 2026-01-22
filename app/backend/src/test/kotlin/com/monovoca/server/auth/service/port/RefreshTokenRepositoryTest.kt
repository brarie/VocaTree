package com.monovoca.server.auth.service.port

import com.monovoca.server.common.config.RedisTestConfig
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Import(RedisTestConfig::class)
class RefreshTokenRepositoryTest(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val redisTemplate: StringRedisTemplate
) : DescribeSpec({

    beforeEach {
        redisTemplate.delete(redisTemplate.keys("*"))
    }

    val refreshToken = "refresh-token-123"
    val userId = "550e8400-e29b-41d4-a716-446655440000"
    val refreshExpMillis = 604800000L // 7일

    describe("save") {
        it("refresh token을 저장할 수 있다.") {
            refreshTokenRepository.save(refreshToken, userId, refreshExpMillis)

            val foundUserId = refreshTokenRepository.findUserIdByRefreshToken(refreshToken)
            foundUserId shouldBe userId
        }
    }

    describe("findUserIdByRefreshToken") {
        it("저장된 refresh token으로 userId를 조회할 수 있다.") {
            refreshTokenRepository.save(refreshToken, userId, refreshExpMillis)

            val foundUserId = refreshTokenRepository.findUserIdByRefreshToken(refreshToken)

            foundUserId shouldBe userId
        }

        it("존재하지 않는 refresh token으로 조회하면 null을 반환한다.") {
            val foundUserId = refreshTokenRepository.findUserIdByRefreshToken("nonexistent-token")

            foundUserId shouldBe null
        }
    }

    describe("deleteByUserId") {
        it("userId로 삭제하면 해당 refresh token으로 조회할 수 없다.") {
            refreshTokenRepository.save(refreshToken, userId, refreshExpMillis)

            refreshTokenRepository.deleteByUserId(userId)

            val foundUserId = refreshTokenRepository.findUserIdByRefreshToken(refreshToken)
            foundUserId shouldBe null
        }

        it("여러 refresh token이 있어도 해당 userId의 모든 토큰이 삭제된다.") {
            val refreshToken2 = "refresh-token-456"
            refreshTokenRepository.save(refreshToken, userId, refreshExpMillis)
            refreshTokenRepository.save(refreshToken2, userId, refreshExpMillis)

            refreshTokenRepository.deleteByUserId(userId)

            refreshTokenRepository.findUserIdByRefreshToken(refreshToken) shouldBe null
            refreshTokenRepository.findUserIdByRefreshToken(refreshToken2) shouldBe null
        }

        it("다른 userId의 토큰은 삭제되지 않는다.") {
            val otherUserId = "660e8400-e29b-41d4-a716-446655440000"
            val otherToken = "other-refresh-token"
            refreshTokenRepository.save(refreshToken, userId, refreshExpMillis)
            refreshTokenRepository.save(otherToken, otherUserId, refreshExpMillis)

            refreshTokenRepository.deleteByUserId(userId)

            refreshTokenRepository.findUserIdByRefreshToken(refreshToken) shouldBe null
            refreshTokenRepository.findUserIdByRefreshToken(otherToken) shouldBe otherUserId
        }
    }
})
