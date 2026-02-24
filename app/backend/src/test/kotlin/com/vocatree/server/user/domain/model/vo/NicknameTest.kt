package com.vocatree.server.user.domain.model.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class NicknameTest : DescribeSpec({
    describe("Nickname 생성") {
        it("올바른 형식의 닉네임이면 객체가 생성된다.") {
            val validNicknames = listOf("닉네임", "nick", "닉네임12", "Nick1234", "가나다라마바사아")
            val nicknames = validNicknames.map { Nickname(it) }
            nicknames.forEachIndexed { index, nickname ->
                nickname.value shouldBe validNicknames[index]
            }
        }
        it("닉네임 길이가 2자 미만이면 IllegalArgumentException 예외가 발생한다.") {
            val invalidNicknames = listOf("a", "가")
            invalidNicknames.forEach {
                shouldThrow<IllegalArgumentException> {
                    Nickname(it)
                }
            }
        }
        it("닉네임 길이가 8자 초과면 IllegalArgumentException 예외가 발생한다.") {
            val invalidNicknames = listOf("닉네임입니다아아아", "nickname9")
            invalidNicknames.forEach {
                shouldThrow<IllegalArgumentException> {
                    Nickname(it)
                }
            }
        }
        it("닉네임에 영문, 숫자, 한글 이외의 문자가 포함되면 IllegalArgumentException 예외가 발생한다.") {
            val invalidNicknames = listOf("nick!", "닉네임@", "nick name", "닉_네임", "닉-네임", "nick😊")
            invalidNicknames.forEach {
                shouldThrow<IllegalArgumentException> {
                    Nickname(it)
                }
            }
        }
    }
})
