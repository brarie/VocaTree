package com.monovoca.server.common.domain.model.vocabulary.vo

data class ExampleVo(
    val sentence: String? = null,
    val translation: String? = null,
) {
    fun isBlank(): Boolean {
        return sentence.isNullOrBlank() && translation.isNullOrBlank()
    }
}