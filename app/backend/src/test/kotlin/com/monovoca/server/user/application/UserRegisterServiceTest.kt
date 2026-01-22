package com.monovoca.server.user.application

import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.common.infrastructure.TestUUIDHolder
import com.monovoca.server.common.infrastructure.config.RedisTestConfig
import com.monovoca.server.user.domain.exception.*
import com.monovoca.server.user.domain.port.PendingUserRepository
import com.monovoca.server.user.domain.port.UserCommandRepository
import com.monovoca.server.user.domain.port.UserQueryRepository
import com.monovoca.server.user.fixture.UserFixture
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

                val tempKey = userRegisterService.signUp(
                    usernameStr = "newuser1",
                    emailStr = "newuser@example.com",
                    nicknameStr = "새유저",
                    rawPassword = "password123!"
                )

                tempKey shouldBe "temp-key-1"

                val savedUser = pendingUserRepository.findByTempKey(tempKey)
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

                shouldThrow<DuplicatedEmailException> {
                    userRegisterService.signUp(
                        usernameStr = "newuser3",
                        emailStr = "existing@example.com",
                        nicknameStr = "새유저",
                        rawPassword = "password123!"
                    )
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

                shouldThrow<DuplicatedUsernameException> {
                    userRegisterService.signUp(
                        usernameStr = "existing2",
                        emailStr = "another@example.com",
                        nicknameStr = "새유저",
                        rawPassword = "password123!"
                    )
                }
            }
        }

        context("이미 가입 진행 중인 이메일로 요청할 때") {
            it("AlreadyRegisteringEmailException이 발생한다") {
                testUUIDHolder.setUUIDs("temp-key-3", "code-3", "temp-key-4", "code-4")

                userRegisterService.signUp(
                    usernameStr = "firstuser",
                    emailStr = "pending@example.com",
                    nicknameStr = "첫번째유저",
                    rawPassword = "password123!"
                )

                shouldThrow<AlreadyRegisteringEmailException> {
                    userRegisterService.signUp(
                        usernameStr = "seconduser",
                        emailStr = "pending@example.com",
                        nicknameStr = "두번째유저",
                        rawPassword = "password123!"
                    )
                }
            }
        }

        context("이미 가입 진행 중인 username으로 요청할 때") {
            it("AlreadyRegisteringUsernameException이 발생한다") {
                testUUIDHolder.setUUIDs("temp-key-5", "code-5", "temp-key-6", "code-6")

                userRegisterService.signUp(
                    usernameStr = "dupuser1",
                    emailStr = "first@example.com",
                    nicknameStr = "첫번째유저",
                    rawPassword = "password123!"
                )

                shouldThrow<AlreadyRegisteringUsernameException> {
                    userRegisterService.signUp(
                        usernameStr = "dupuser1",
                        emailStr = "second@example.com",
                        nicknameStr = "두번째유저",
                        rawPassword = "password123!"
                    )
                }
            }
        }
    }

    describe("certificate") {
        context("유효한 인증 코드로 요청할 때") {
            it("사용자가 정식으로 저장되고 pending 데이터가 삭제된다") {
                testUUIDHolder.setUUIDs("temp-key-cert", "code-cert")

                val tempKey = userRegisterService.signUp(
                    usernameStr = "certuser",
                    emailStr = "certuser@example.com",
                    nicknameStr = "인증유저",
                    rawPassword = "password123!"
                )

                userRegisterService.certificate("code-cert")

                userQueryRepository.existsByUsername(Username("certuser")) shouldBe true
                pendingUserRepository.findByTempKey(tempKey) shouldBe null
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

                userRegisterService.signUp(
                    usernameStr = "certuser2",
                    emailStr = "certuser2@example.com",
                    nicknameStr = "인증유저2",
                    rawPassword = "password123!"
                )
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

                val tempKey = userRegisterService.signUp(
                    usernameStr = "rsuser1",
                    emailStr = "rsuser1@example.com",
                    nicknameStr = "재전송유저",
                    rawPassword = "password123!"
                )

                userRegisterService.resendCertification(tempKey)

                userRegisterService.certificate("new-code")
                userQueryRepository.existsByUsername(Username("rsuser1")) shouldBe true
            }

            it("이전 인증 코드는 무효화된다") {
                testUUIDHolder.setUUIDs("temp-key-rs2", "old-code-2", "new-code-2")

                val tempKey = userRegisterService.signUp(
                    usernameStr = "rsuser2",
                    emailStr = "rsuser2@example.com",
                    nicknameStr = "재전송유저2",
                    rawPassword = "password123!"
                )

                userRegisterService.resendCertification(tempKey)

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
