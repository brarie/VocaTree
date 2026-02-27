package com.vocatree.server.common.domain.model.vocabulary.vo

import com.fasterxml.jackson.annotation.JsonIgnore

data class ExampleVo(
    val sentence: String? = null,
    val translation: String? = null,
) {
    @JsonIgnore
    fun isBlank(): Boolean {
        return sentence.isNullOrBlank() && translation.isNullOrBlank()
    }
}