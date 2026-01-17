package com.monovoca.server.user.domain.model.vo

object UsernamePolicy {
    const val regex = "^[A-Za-z0-9]{4,10}$"
    val pattern = regex.toRegex()
    const val message = "아이디는 영문, 숫자를 조합하여 4~10자로 설정해주세요."
}

@JvmInline
value class Username(val value: String) {
    init {
        require(UsernamePolicy.pattern.matches(value)) { UsernamePolicy.message }
    }
}
