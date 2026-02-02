package com.monovoca.server.auth.domain.port

import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.user.domain.port.UserRepository
import com.monovoca.server.user.fixture.UserFixture
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
class AuthUserQueryRepositoryTest(
    private val authUserRepository: AuthUserRepository,
    private val userRepository: UserRepository
) : DescribeSpec({

    describe("findAuthUserById") {
        it("id가 존재하면 AuthUser를 반환한다.") {
            val user = UserFixture.create(
                username = "testuser1",
                email = "testuser1@example.com"
            )
            val savedUser = userRepository.save(user)

            val authUser = authUserRepository.findAuthUserById(savedUser.id!!)

            authUser shouldNotBe null
            authUser!!.id shouldBe savedUser.id
            authUser.username.value shouldBe "testuser1"
        }

        it("id가 존재하지 않으면 null을 반환한다.") {
            val authUser = authUserRepository.findAuthUserById("00000000-0000-0000-0000-000000000000")

            authUser shouldBe null
        }
    }

    describe("findAuthUserByUsername") {
        it("username이 존재하면 AuthUser를 반환한다.") {
            val user = UserFixture.create(
                username = "testuser2",
                email = "testuser2@example.com"
            )
            val savedUser = userRepository.save(user)

            val authUser = authUserRepository.findAuthUserByUsername(Username("testuser2"))

            authUser shouldNotBe null
            authUser!!.username.value shouldBe "testuser2"
            authUser.id shouldBe savedUser.id
        }

        it("username이 존재하지 않으면 null을 반환한다.") {
            val authUser = authUserRepository.findAuthUserByUsername(Username("nonexist"))
            authUser shouldBe null
        }
    }
})