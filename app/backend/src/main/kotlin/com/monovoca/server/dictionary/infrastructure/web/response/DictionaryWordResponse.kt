package com.monovoca.server.dictionary.infrastructure.web.response

import com.monovoca.server.common.domain.model.vo.enumerated.PartOfSpeech
import com.monovoca.server.dictionary.domain.model.word.PublicExample
import com.monovoca.server.dictionary.domain.model.word.PublicSense
import com.monovoca.server.dictionary.domain.model.word.PublicWord

data class PublicWordResponse(
    val id: Long,
    val spelling: String,
    val senses: List<Sense>,
) {
    data class Sense(
        val id: Long,
        val definition: String,
        val engDefinition: String?,
        val britishPhonetic: String?,
        val americanPhonetic: String?,
        val synonyms: List<String>,
        val antonyms: List<String>,
        val partOfSpeech: PartOfSpeech?,
        val displayOrder: Int,
        val examples: List<Example>,
    ) {
        companion object {
            fun from(sense: PublicSense): Sense {
                return Sense(
                    id = sense.id!!,
                    definition = sense.definition,
                    engDefinition = sense.engDefinition,
                    britishPhonetic = sense.britishPhonetic,
                    americanPhonetic = sense.americanPhonetic,
                    synonyms = sense.synonyms.map { it.value },
                    antonyms = sense.antonyms.map { it.value },
                    partOfSpeech = sense.partOfSpeech,
                    displayOrder = sense.displayOrder,
                    examples = sense.examples.map { Example.from(it) },
                )
            }
        }
    }

    data class Example(
        val id: Long,
        val sentence: String,
        val translation: String?,
        val isOfficial: Boolean,
        val sourceInfo: String?,
        val displayOrder: Int,
    ) {
        companion object {
            fun from(example: PublicExample): Example {
                return Example(
                    id = example.id!!,
                    sentence = example.sentence,
                    translation = example.translation,
                    isOfficial = example.isOfficial,
                    sourceInfo = example.sourceInfo,
                    displayOrder = example.displayOrder,
                )
            }
        }
    }

    companion object {
        fun from(word: PublicWord): PublicWordResponse {
            return PublicWordResponse(
                id = word.id!!,
                spelling = word.spelling.value,
                senses = word.senses.map { Sense.from(it) },
            )
        }
    }
}