package com.monovoca.server.user.domain.port

import com.monovoca.server.common.domain.model.vo.Username
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.user.fixture.UserFixture
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
class UserQueryRepositoryTest(
    private val userRepository: UserRepository,
) : DescribeSpec({

    describe("save") {
        it("새로운 유저를 저장하면 유저의 ID가 할당된다.") {
            var user = UserFixture.create(
                username = "testuser1",
                email = "monovoca@naver.com"
            )
            user = userRepository.save(user)
            user.id shouldNotBe null
        }
        it("기존 유저를 수정하면 변경된 내용이 반영된다.") {
            var user = UserFixture.create(
                username = "testuser1",
                email = "monovoca@naver.com"
            )
            user = userRepository.save(user)
            user = user.updateNickname(Nickname("수정닉네임"))
            val updated = userRepository.save(user)
            updated.nickname shouldBe Nickname("수정닉네임")
        }
        it("존재하지 않는 ID로 유저를 수정하면 IllegalStateException 발생한다.") {
            val user = UserFixture.create(
                id = "00000000-0000-0000-0000-000000000000",
                username = "testuser1",
                email = "monovoca@naver.com"
            )
            shouldThrow<IllegalStateException> {
                userRepository.save(user)
            }
        }
    }
    describe("existsByUsername") {
        it("username이 존재하면 true를 반환한다.") {
            val user = UserFixture.create(
                username = "testuser1",
                email = "monovoca@naver.com"
            )
            userRepository.save(user)
            val exists = userRepository.existsByUsername(Username("testuser1"))
            exists shouldBe true
        }
        it("username이 존재하지 않으면 false를 반환한다.") {
            val exists = userRepository.existsByUsername(Username("nouser1234"))
            exists shouldBe false
        }
    }
    describe("existsByEmail") {
        it("email이 존재하면 true를 반환한다.") {
            val user = UserFixture.create(
                username = "testuser1",
                email = "monovoca@naver.com"
            )
            userRepository.save(user)
            val exists = userRepository.existsByEmail(Email("monovoca@naver.com"))
            exists shouldBe true
        }
        it("email이 존재하지 않으면 false를 반환한다.") {
            val exists = userRepository.existsByEmail(Email("nonexistent@naver.com"))
            exists shouldBe false
        }
    }
    describe("findById") {
        it("존재하는 유저 id로 조회하면 User를 반환한다.") {
            val user = UserFixture.create(
                username = "finduser",
                email = "finduser@example.com"
            )
            val savedUser = userRepository.save(user)
            val foundUser = userRepository.findById(savedUser.id!!)
            foundUser shouldNotBe null
            foundUser!!.id shouldBe savedUser.id
            foundUser.username.value shouldBe "finduser"
            foundUser.email.value shouldBe "finduser@example.com"
        }
        it("존재하지 않는 유저 id로 조회하면 null을 반환한다.") {
            val foundUser = userRepository.findById("00000000-0000-0000-0000-000000000000")
            foundUser shouldBe null
        }
    }
})
