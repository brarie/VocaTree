package com.vocatree.server.common.domain.model.vocabulary.vo.components

enum class PartOfSpeech(val koreanName: String) {
    NOUN("명사"),
    VERB("동사"),
    ADJECTIVE("형용사"),
    ADVERB("부사"),
    PRONOUN("대명사"),
    PREPOSITION("전치사"),
    CONJUNCTION("접속사"),
    INTERJECTION("감탄사"),
    DETERMINER("관사"),
    PHRASE("구"),
    IDIOM("숙어");

    companion object {
        fun fromString(value: String): PartOfSpeech {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid part of speech: $value")
        }
    }
}