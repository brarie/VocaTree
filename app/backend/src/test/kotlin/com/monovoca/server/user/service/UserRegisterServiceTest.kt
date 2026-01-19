package com.monovoca.server.user.service

import com.monovoca.server.common.config.RedisTestConfig
import com.monovoca.server.common.infrastructure.TestUUIDHolder
import com.monovoca.server.user.controller.request.SignUpRequest
import com.monovoca.server.user.domain.exception.*
import com.monovoca.server.user.domain.model.vo.Username
import com.monovoca.server.user.domain.repository.UserCommandRepository
import com.monovoca.server.user.domain.repository.UserQueryRepository
import com.monovoca.server.user.fixture.UserFixture
import com.monovoca.server.user.service.port.PendingUserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Import(RedisTestConfig::class)
@Transactional
class UserRegisterServiceTest(
    private val userRegisterService: UserRegisterService,
    private val userQueryRepository: UserQueryRepository,
    private val userCommandRepository: UserCommandRepository,
    private val pendingUserRepository: PendingUserRepository,
    private val redisTemplate: StringRedisTemplate,
    private val testUUIDHolder: TestUUIDHolder
) : DescribeSpec({

    beforeEach {
        redisTemplate.delete(redisTemplate.keys("*"))
        testUUIDHolder.reset()
    }

    describe("signUp") {
        context("정상적인 회원가입 요청일 때") {
            it("tempKey를 반환하고 PendingUserRepository에 사용자 정보가 저장된다") {
                testUUIDHolder.setUUIDs("temp-key-1", "code-1")

                val request = SignUpRequest(
                    username = "newuser1",
                    email = "newuser@example.com",
                    nickname = "새유저",
                    rawPassword = "password123!"
                )

                val response = userRegisterService.signUp(request)

                response.tempKey shouldBe "temp-key-1"

                val savedUser = pendingUserRepository.findByTempKey(response.tempKey)
                savedUser shouldNotBe null
                savedUser!!.username.value shouldBe "newuser1"
                savedUser.email.value shouldBe "newuser@example.com"
            }
        }

        context("이미 가입된 이메일로 요청할 때") {
            it("DuplicatedEmailException이 발생한다") {
                val existingUser = UserFixture.create(
                    username = "existing1",
                    email = "existing@example.com"
                )
                userCommandRepository.save(existingUser)

                val request = SignUpRequest(
                    username = "newuser3",
                    email = "existing@example.com",
                    nickname = "새유저",
                    rawPassword = "password123!"
                )

                shouldThrow<DuplicatedEmailException> {
                    userRegisterService.signUp(request)
                }
            }
        }

        context("이미 가입된 username으로 요청할 때") {
            it("DuplicatedUsernameException이 발생한다") {
                val existingUser = UserFixture.create(
                    username = "existing2",
                    email = "existing2@example.com"
                )
                userCommandRepository.save(existingUser)

                val request = SignUpRequest(
                    username = "existing2",
                    email = "another@example.com",
                    nickname = "새유저",
                    rawPassword = "password123!"
                )

                shouldThrow<DuplicatedUsernameException> {
                    userRegisterService.signUp(request)
                }
            }
        }

        context("이미 가입 진행 중인 이메일로 요청할 때") {
            it("AlreadyRegisteringEmailException이 발생한다") {
                testUUIDHolder.setUUIDs("temp-key-3", "code-3", "temp-key-4", "code-4")

                val firstRequest = SignUpRequest(
                    username = "firstuser",
                    email = "pending@example.com",
                    nickname = "첫번째유저",
                    rawPassword = "password123!"
                )
                userRegisterService.signUp(firstRequest)

                val secondRequest = SignUpRequest(
                    username = "seconduser",
                    email = "pending@example.com",
                    nickname = "두번째유저",
                    rawPassword = "password123!"
                )

                shouldThrow<AlreadyRegisteringEmailException> {
                    userRegisterService.signUp(secondRequest)
                }
            }
        }

        context("이미 가입 진행 중인 username으로 요청할 때") {
            it("AlreadyRegisteringUsernameException이 발생한다") {
                testUUIDHolder.setUUIDs("temp-key-5", "code-5", "temp-key-6", "code-6")

                val firstRequest = SignUpRequest(
                    username = "dupuser1",
                    email = "first@example.com",
                    nickname = "첫번째유저",
                    rawPassword = "password123!"
                )
                userRegisterService.signUp(firstRequest)

                val secondRequest = SignUpRequest(
                    username = "dupuser1",
                    email = "second@example.com",
                    nickname = "두번째유저",
                    rawPassword = "password123!"
                )

                shouldThrow<AlreadyRegisteringUsernameException> {
                    userRegisterService.signUp(secondRequest)
                }
            }
        }
    }

    describe("certificate") {
        context("유효한 인증 코드로 요청할 때") {
            it("사용자가 정식으로 저장되고 pending 데이터가 삭제된다") {
                testUUIDHolder.setUUIDs("temp-key-cert", "code-cert")

                val signUpRequest = SignUpRequest(
                    username = "certuser",
                    email = "certuser@example.com",
                    nickname = "인증유저",
                    rawPassword = "password123!"
                )
                val signUpResponse = userRegisterService.signUp(signUpRequest)

                userRegisterService.certificate("code-cert")

                userQueryRepository.existsByUsername(Username("certuser")) shouldBe true
                pendingUserRepository.findByTempKey(signUpResponse.tempKey) shouldBe null
            }
        }

        context("유효하지 않은 인증 코드로 요청할 때") {
            it("InvalidCertificationCodeException이 발생한다") {
                shouldThrow<InvalidCertificationCodeException> {
                    userRegisterService.certificate("invalid-code-12345")
                }
            }
        }

        context("이미 사용된 인증 코드로 요청할 때") {
            it("InvalidCertificationCodeException이 발생한다") {
                testUUIDHolder.setUUIDs("temp-key-cert2", "code-cert2")

                val signUpRequest = SignUpRequest(
                    username = "certuser2",
                    email = "certuser2@example.com",
                    nickname = "인증유저2",
                    rawPassword = "password123!"
                )
                userRegisterService.signUp(signUpRequest)
                userRegisterService.certificate("code-cert2")

                shouldThrow<InvalidCertificationCodeException> {
                    userRegisterService.certificate("code-cert2")
                }
            }
        }
    }

    describe("resendCertification") {
        context("유효한 tempKey로 요청할 때") {
            it("새로운 인증 코드가 생성된다") {
                testUUIDHolder.setUUIDs("temp-key-rs", "old-code", "new-code")

                val signUpRequest = SignUpRequest(
                    username = "rsuser1",
                    email = "rsuser1@example.com",
                    nickname = "재전송유저",
                    rawPassword = "password123!"
                )
                val signUpResponse = userRegisterService.signUp(signUpRequest)

                userRegisterService.resendCertification(signUpResponse.tempKey)

                userRegisterService.certificate("new-code")
                userQueryRepository.existsByUsername(Username("rsuser1")) shouldBe true
            }

            it("이전 인증 코드는 무효화된다") {
                testUUIDHolder.setUUIDs("temp-key-rs2", "old-code-2", "new-code-2")

                val signUpRequest = SignUpRequest(
                    username = "rsuser2",
                    email = "rsuser2@example.com",
                    nickname = "재전송유저2",
                    rawPassword = "password123!"
                )
                val signUpResponse = userRegisterService.signUp(signUpRequest)

                userRegisterService.resendCertification(signUpResponse.tempKey)

                shouldThrow<InvalidCertificationCodeException> {
                    userRegisterService.certificate("old-code-2")
                }
            }
        }

        context("유효하지 않은 tempKey로 요청할 때") {
            it("InvalidTempKeyException이 발생한다") {
                shouldThrow<InvalidTempKeyException> {
                    userRegisterService.resendCertification("invalid-temp-key")
                }
            }
        }
    }
})
