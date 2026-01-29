package com.monovoca.server.auth.application

import com.monovoca.server.auth.domain.port.AccessTokenProvider
import com.monovoca.server.auth.fixture.AuthUserFixture
import com.monovoca.server.common.domain.model.vo.enumerated.UserRole
import com.monovoca.server.common.infrastructure.holder.TestDateHolder
import com.monovoca.server.common.infrastructure.holder.TestUUIDHolder
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ApplyExtension(SpringExtension::class)
class TokenIssuerTest(
    private val tokenIssuer: TokenIssuer,
    private val testUUIDHolder: TestUUIDHolder,
    private val testDateHolder: TestDateHolder,
    private val accessTokenProvider: AccessTokenProvider,
    @param:Value("\${app.jwt.access-token-expiration-ms}") private val accessExpMillis: Long,
    @param:Value("\${app.jwt.refresh-token-expiration-ms}") private val refreshExpMillis: Long
) : DescribeSpec({

    beforeEach {
        testUUIDHolder.reset()
        testDateHolder.reset()
    }

    describe("issueTokenResult") {
        it("TokenResult의 expMillis가 설정값과 일치한다.") {
            val authUser = AuthUserFixture.create()

            val result = tokenIssuer.issueTokenResult(authUser)

            result.accessExpMillis shouldBe accessExpMillis
            result.refreshExpMillis shouldBe refreshExpMillis
        }

        it("accessToken의 issuedAt, expiresAt이 정확히 계산된다.") {
            val fixedTime = 10000000000000L
            testDateHolder.setTimes(fixedTime)
            val authUser = AuthUserFixture.create()

            val result = tokenIssuer.issueTokenResult(authUser)

            val parsedToken = accessTokenProvider.parse(result.accessToken)
            parsedToken.issuedAt shouldBe fixedTime
            parsedToken.expiresAt shouldBe fixedTime + accessExpMillis
        }

        it("accessToken에 userId, role이 포함된다.") {
            val authUser = AuthUserFixture.create(
                id = "test-user-id",
                role = UserRole.ADMIN
            )

            val result = tokenIssuer.issueTokenResult(authUser)
            val parsedToken = accessTokenProvider.parse(result.accessToken)

            parsedToken.userId shouldBe "test-user-id"
            parsedToken.role shouldBe UserRole.ADMIN
        }

        it("refreshToken은 UUIDHolder에서 생성된 값이다.") {
            testUUIDHolder.setUUIDs("custom-refresh-uuid")
            val authUser = AuthUserFixture.create()

            val result = tokenIssuer.issueTokenResult(authUser)

            result.refreshToken shouldBe "custom-refresh-uuid"
        }

        it("accessToken은 파싱 가능한 JWT 형식이다.") {
            val authUser = AuthUserFixture.create()

            val result = tokenIssuer.issueTokenResult(authUser)

            result.accessToken.shouldNotBeEmpty()
            result.accessToken.split(".").size shouldBe 3
        }
    }
})
