package com.vocatree.server.user.domain.model.vo

object NicknamePolicy {
    const val regex = "^[A-Za-z0-9가-힣]{2,8}$"
    val pattern = regex.toRegex()
    const val message = "닉네임은 영문, 숫자, 한글을 조합하여 2~8자로 설정해주세요."
}

@JvmInline
value class Nickname(val value: String) {
    init {
        require(NicknamePolicy.pattern.matches(value)) { NicknamePolicy.message }
    }
}
