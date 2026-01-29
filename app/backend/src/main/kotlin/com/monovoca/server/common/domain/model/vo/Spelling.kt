package com.monovoca.server.common.domain.model.vo

object SpellingPolicy {
    const val regex = "^[A-Za-z\\s\\-'\\[\\]()]{1,50}$"
    val pattern = regex.toRegex()
    const val message = "철자는 50자 이하의 영문, 공백, 하이픈(-), 아포스트로피('), 대소괄호로 설정해주세요."
}

@JvmInline
value class Spelling(val value: String) {
    init {
        require(SpellingPolicy.pattern.matches(value)) { SpellingPolicy.message }
    }
}