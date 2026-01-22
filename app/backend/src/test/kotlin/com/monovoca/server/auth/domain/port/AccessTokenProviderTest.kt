package com.monovoca.server.auth.domain.port

import com.monovoca.server.auth.domain.exception.InvalidAccessTokenException
import com.monovoca.server.auth.domain.exception.TokenExpiredException
import com.monovoca.server.auth.domain.model.AuthToken
import com.monovoca.server.common.domain.model.vo.UserRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ApplyExtension(SpringExtension::class)
class AccessTokenProviderTest(
    private val accessTokenProvider: AccessTokenProvider
) : DescribeSpec({

    // JWT는 초 단위로 저장하므로 밀리초는 1000의 배수로 설정
    val userId = "550e8400-e29b-41d4-a716-446655440000"
    val role = UserRole.USER
    // 1000의 배수 (그 아래는 000으로 사라짐) 이는 실 운영에 영향을 미치지 않으므로 테스트에서 상황 강제
    val issuedAt = 1700000000000L
    val expiresAt = 10000000000000L  // 2286년 10월 16일. 충분히 미래로 설정해야 함.

    describe("issue") {
        it("AuthToken을 JWT 문자열로 발급한다.") {
            val authToken = AuthToken(
                userId = userId,
                role = role,
                issuedAt = issuedAt,
                expiresAt = expiresAt
            )

            val jwt = accessTokenProvider.issue(authToken)

            jwt.shouldNotBeEmpty()
            jwt.split(".").size shouldBe 3
        }
    }

    describe("parse") {
        it("유효한 JWT를 파싱하면 AuthToken을 반환한다.") {
            val authToken = AuthToken(
                userId = userId,
                role = role,
                issuedAt = issuedAt,
                expiresAt = expiresAt
            )
            val jwt = accessTokenProvider.issue(authToken)

            val parsed = accessTokenProvider.parse(jwt)

            parsed.userId shouldBe userId
            parsed.role shouldBe role
            parsed.issuedAt shouldBe issuedAt
            parsed.expiresAt shouldBe expiresAt
        }

        it("만료된 토큰을 파싱하면 TokenExpiredException이 발생한다.") {
            val expiredToken = AuthToken(
                userId = userId,
                role = role,
                issuedAt  = 1600000000000L,  // 과거
                expiresAt = 1600000001000L  // 이미 만료됨
            )
            val jwt = accessTokenProvider.issue(expiredToken)

            shouldThrow<TokenExpiredException> {
                accessTokenProvider.parse(jwt)
            }
        }

        it("잘못된 형식의 토큰을 파싱하면 InvalidAccessTokenException이 발생한다.") {
            val invalidJwt = "invalid.jwt.token"

            shouldThrow<InvalidAccessTokenException> {
                accessTokenProvider.parse(invalidJwt)
            }
        }

        it("변조된 토큰을 파싱하면 InvalidAccessTokenException이 발생한다.") {
            val authToken = AuthToken(
                userId = userId,
                role = role,
                issuedAt = issuedAt,
                expiresAt = expiresAt
            )
            val jwt = accessTokenProvider.issue(authToken)
            val tamperedJwt = jwt.dropLast(5) + "xxxxx"  // signature 변조

            shouldThrow<InvalidAccessTokenException> {
                accessTokenProvider.parse(tamperedJwt)
            }
        }
    }
})
