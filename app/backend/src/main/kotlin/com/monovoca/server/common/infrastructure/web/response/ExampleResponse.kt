package com.monovoca.server.common.infrastructure.web.response

import com.monovoca.server.common.domain.model.vocabulary.vo.ExampleVo

data class ExampleResponse(
    val sentence: String?,
    val translation: String?
) {
    companion object {
        fun from(exampleVo: ExampleVo): ExampleResponse {
            return ExampleResponse(
                sentence = exampleVo.sentence,
                translation = exampleVo.translation
            )
        }
    }
}
