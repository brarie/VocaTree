package com.monovoca.server.dictionary.infrastructure.web.response

import com.monovoca.server.common.infrastructure.web.response.SenseResponse
import com.monovoca.server.common.infrastructure.web.response.ExampleResponse
import com.monovoca.server.dictionary.domain.model.word.PublicExample
import com.monovoca.server.dictionary.domain.model.word.PublicSense
import com.monovoca.server.dictionary.domain.model.word.PublicWord

data class PublicWordResponse(
    val id: Long,
    val spelling: String,
    val slug: String,
    val senses: List<Sense>,
) {
    data class Sense(
        val id: Long,
        val sense: SenseResponse,
        val displayOrder: Int,
        val examples: List<Example>,
    ) {
        companion object {
            fun from(publicSense: PublicSense): Sense {
                return Sense(
                    id = publicSense.id!!,
                    sense = SenseResponse.from(publicSense.sense),
                    displayOrder = publicSense.displayOrder,
                    examples = publicSense.examples.map { Example.from(it) },
                )
            }
        }
    }

    data class Example(
        val id: Long,
        val example: ExampleResponse,
        val isOfficial: Boolean,
        val sourceInfo: String?,
        val displayOrder: Int,
    ) {
        companion object {
            fun from(publicExample: PublicExample): Example {
                return Example(
                    id = publicExample.id!!,
                    example = ExampleResponse.from(publicExample.example),
                    isOfficial = publicExample.isOfficial,
                    sourceInfo = publicExample.sourceInfo,
                    displayOrder = publicExample.displayOrder,
                )
            }
        }
    }

    companion object {
        fun from(word: PublicWord): PublicWordResponse {
            return PublicWordResponse(
                id = word.id!!,
                spelling = word.spelling.value,
                slug = word.slug!!,
                senses = word.senses.map { Sense.from(it) },
            )
        }
    }
}