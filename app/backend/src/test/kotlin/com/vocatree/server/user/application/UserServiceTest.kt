package com.vocatree.server.user.application

import com.vocatree.server.user.domain.exception.UserNotFoundException
import com.vocatree.server.user.domain.port.UserRepository
import com.vocatree.server.user.fixture.UserFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ApplyExtension(SpringExtension::class)
@Transactional
class UserServiceTest(
    private val userService: UserService,
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("findById") {
        context("존재하는 유저 id로 요청할 때") {
            it("User를 반환한다.") {
                val user = UserFixture.create(
                    username = "testuser",
                    email = "testuser@example.com",
                    nickname = "테스트유저"
                )
                val savedUser = userRepository.save(user)

                val foundUser = userService.findById(savedUser.id!!)

                foundUser shouldNotBe null
                foundUser.id shouldBe savedUser.id
                foundUser.username.value shouldBe "testuser"
                foundUser.email.value shouldBe "testuser@example.com"
                foundUser.nickname.value shouldBe "테스트유저"
            }
        }

        context("존재하지 않는 유저 id로 요청할 때") {
            it("UserNotFoundException이 발생한다.") {
                shouldThrow<UserNotFoundException> {
                    userService.findById("00000000-0000-0000-0000-000000000000")
                }
            }
        }
    }
})
