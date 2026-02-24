package com.vocatree.server.user.domain.model.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EmailTest : DescribeSpec({
    describe("Email 생성") {
        it("올바른 형식의 이메일이면 객체가 생성된다.") {
            val validEmails = listOf("vocatree@naver.com", "vocatree@gmail.com", "abx@dd.org")
            val emails = validEmails.map { Email(it) }
            emails.forEachIndexed { index, email ->
                email.value shouldBe validEmails[index]
            }
        }
        it("올바르지 않은 형식의 이메일이면 IllegalArgumentException 예외가 발생한다.") {
            val invalidEmails = listOf(
                "vocatree", "vocatree@naver", "vocatree@.com",
                "vocatree@naver.", "@naver.com", "@@@", "!@#.c"
            )
            invalidEmails.forEach {
                shouldThrow<IllegalArgumentException> {
                    Email(it)
                }
            }
        }
    }
})