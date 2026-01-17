package com.monovoca.server.user.domain.repository

import com.monovoca.server.user.domain.exception.UserNotFoundException
import com.monovoca.server.user.domain.model.User
import com.monovoca.server.user.domain.model.vo.Email
import com.monovoca.server.user.domain.model.vo.EncryptedPassword
import com.monovoca.server.user.domain.model.vo.Nickname
import com.monovoca.server.user.domain.model.vo.Username
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
class UserCommandRepositoryTest(
    private val userCommandRepository: UserCommandRepository
) : DescribeSpec({
    describe("save") {
        it("새로운 유저를 저장하면 유저의 ID가 할당된다.") {
            var user = User(
                username = Username("testuser1"),
                email = Email("monovoca@naver.com"),
                nickname = Nickname("테스트유저"),
                password = EncryptedPassword.fromHash("12345678")
            )
            user = userCommandRepository.save(user)
            user.id shouldNotBe null
        }
        it("기존 유저를 수정하면 변경된 내용이 반영된다.") {
            var user = User(
                username = Username("testuser1"),
                email = Email("monovoca@naver.com"),
                nickname = Nickname("테스트유저"),
                password = EncryptedPassword.fromHash("12345678")
            )
            user = userCommandRepository.save(user)
            user = user.updateNickname(Nickname("수정닉네임"))
            val updated = userCommandRepository.save(user)
            updated.nickname shouldBe Nickname("수정닉네임")
        }
        it("존재하지 않는 ID로 유저를 수정하면 UserNotFoundException예외가 발생한다.") {
            var user = User(
                id = 9999L,
                username = Username("testuser1"),
                email = Email("monovoca@naver.com"),
                nickname = Nickname("테스트유저"),
                password = EncryptedPassword.fromHash("12345678")
            )
            shouldThrow<UserNotFoundException> {
                userCommandRepository.save(user)
            }
        }
    }
})