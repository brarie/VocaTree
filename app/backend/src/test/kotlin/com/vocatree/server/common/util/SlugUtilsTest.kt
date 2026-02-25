package com.vocatree.server.common.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class SlugUtilsTest : DescribeSpec({
    describe("generateSlug") {
        it("한글 제목은 하이픈으로 연결되고 id가 뒤에 붙는다.") {
            generateSlug("토익 필수 단어", 42) shouldBe "토익-필수-단어-42"
        }

        it("영문 제목은 소문자로 변환되고 id가 뒤에 붙는다.") {
            generateSlug("TOEIC Vocabulary", 7) shouldBe "toeic-vocabulary-7"
        }

        it("한글과 영문이 섞인 제목도 처리된다.") {
            generateSlug("TOEIC 필수 단어", 3) shouldBe "toeic-필수-단어-3"
        }

        it("앞뒤 공백은 제거된다.") {
            generateSlug("  토익 단어  ", 1) shouldBe "토익-단어-1"
        }

        it("연속된 공백은 하나의 하이픈으로 변환된다.") {
            generateSlug("토익   단어", 1) shouldBe "토익-단어-1"
        }

        it("허용되지 않는 특수문자는 제거된다.") {
            generateSlug("토익 (필수) 단어!", 5) shouldBe "토익-필수-단어-5"
        }

        it("제목이 특수문자로만 이루어지면 -id 형태로 반환된다.") {
            generateSlug("!@#$%^&*()", 99) shouldBe "-99"
        }

        it("하이픈은 제목에 그대로 유지된다.") {
            generateSlug("영어-한국어 단어장", 10) shouldBe "영어-한국어-단어장-10"
        }
    }
})
