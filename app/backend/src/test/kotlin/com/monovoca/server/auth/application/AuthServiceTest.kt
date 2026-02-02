package com.monovoca.server.auth.application

import com.monovoca.server.auth.domain.exception.AuthUserNotFoundException
import com.monovoca.server.auth.domain.exception.InvalidPasswordException
import com.monovoca.server.auth.domain.exception.InvalidRefreshTokenException
import com.monovoca.server.auth.domain.port.RefreshTokenRepository
import com.monovoca.server.common.domain.model.vo.EncryptedPassword
import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.common.infrastructure.config.RedisTestConfig
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.user.domain.port.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Import(RedisTestConfig::class)
@Transactional
class AuthServiceTest(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: StringRedisTemplate
) : DescribeSpec({

    beforeEach {
        redisTemplate.delete(redisTemplate.keys("*"))
    }

    val rawPassword = "password123!"

    fun createAndSaveUser(username: String, email: String): User {
        val user = User(
            username = Username(username),
            email = Email(email),
            nickname = Nickname("테스트유저"),
            password = EncryptedPassword.encrypt(rawPassword, passwordEncoder)
        )
        return userRepository.save(user)
    }

    describe("login") {
        context("정상적인 로그인 요청일 때") {
            it("TokenResult를 반환한다.") {
                createAndSaveUser("loginuser1", "loginuser1@example.com")

                val result = authService.login("loginuser1", rawPassword)

                result.accessToken.shouldNotBeEmpty()
                result.refreshToken.shouldNotBeEmpty()
                result.accessExpMillis shouldNotBe 0L
                result.refreshExpMillis shouldNotBe 0L
            }

            it("refresh token이 저장된다.") {
                val user = createAndSaveUser("loginuser2", "loginuser2@example.com")

                val result = authService.login("loginuser2", rawPassword)

                val storedUserId = refreshTokenRepository.findUserIdByRefreshToken(result.refreshToken)
                storedUserId shouldBe user.id
            }
        }

        context("존재하지 않는 username으로 로그인 요청할 때") {
            it("AuthUserNotFoundException이 발생한다.") {
                shouldThrow<AuthUserNotFoundException> {
                    authService.login("nonexist", rawPassword)
                }
            }
        }

        context("잘못된 비밀번호로 로그인 요청할 때") {
            it("InvalidPasswordException이 발생한다.") {
                createAndSaveUser("loginuser3", "loginuser3@example.com")

                shouldThrow<InvalidPasswordException> {
                    authService.login("loginuser3", "wrongpassword!")
                }
            }
        }
    }

    describe("logout") {
        it("로그아웃하면 refresh token이 삭제된다.") {
            val user = createAndSaveUser("logoutuser", "logoutuser1@example.com")

            val loginResult = authService.login("logoutuser", rawPassword)

            authService.logout(user.id!!)

            val storedUserId = refreshTokenRepository.findUserIdByRefreshToken(loginResult.refreshToken)
            storedUserId shouldBe null
        }
    }

    describe("refresh") {
        context("유효한 refresh token으로 요청할 때") {
            it("새로운 TokenResult를 반환한다.") {
                createAndSaveUser("refresh1", "refreshuser1@example.com")

                val loginResult = authService.login("refresh1", rawPassword)

                val refreshResult = authService.refresh(loginResult.refreshToken)

                refreshResult.accessToken.shouldNotBeEmpty()
                refreshResult.refreshToken.shouldNotBeEmpty()
            }

            it("기존 refresh token은 무효화된다 (token rotation).") {
                createAndSaveUser("refresh2", "refreshuser2@example.com")

                val loginResult = authService.login("refresh2", rawPassword)
                val oldRefreshToken = loginResult.refreshToken

                authService.refresh(oldRefreshToken)

                shouldThrow<InvalidRefreshTokenException> {
                    authService.refresh(oldRefreshToken)
                }
            }

            it("새로운 refresh token으로 다시 refresh할 수 있다.") {
                createAndSaveUser("refresh3", "refreshuser3@example.com")

                val loginResult = authService.login("refresh3", rawPassword)

                val firstRefresh = authService.refresh(loginResult.refreshToken)
                val secondRefresh = authService.refresh(firstRefresh.refreshToken)

                secondRefresh.accessToken.shouldNotBeEmpty()
                secondRefresh.refreshToken.shouldNotBeEmpty()
            }
        }

        context("유효하지 않은 refresh token으로 요청할 때") {
            it("InvalidRefreshTokenException이 발생한다.") {
                shouldThrow<InvalidRefreshTokenException> {
                    authService.refresh("invalid-refresh-token")
                }
            }
        }
    }
})
