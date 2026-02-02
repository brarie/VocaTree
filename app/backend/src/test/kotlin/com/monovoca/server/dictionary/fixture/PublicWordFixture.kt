package com.monovoca.server.dictionary.fixture

import com.monovoca.server.common.domain.model.vo.Spelling
import com.monovoca.server.common.domain.model.vo.enumerated.PartOfSpeech
import com.monovoca.server.dictionary.domain.model.word.PublicExample
import com.monovoca.server.dictionary.domain.model.word.PublicSense
import com.monovoca.server.dictionary.domain.model.word.PublicWord

object PublicWordFixture {

    fun createWord(
        id: Long? = null,
        spelling: String = "test",
        senses: List<PublicSense> = emptyList()
    ): PublicWord {
        return PublicWord(
            id = id,
            spelling = Spelling(spelling),
            senses = senses
        )
    }

    fun createSense(
        id: Long? = null,
        definition: String = "테스트 뜻",
        engDefinition: String? = "test definition",
        britishPhonetic: String? = "/test/",
        americanPhonetic: String? = "/test/",
        synonyms: List<String> = emptyList(),
        antonyms: List<String> = emptyList(),
        partOfSpeech: PartOfSpeech? = PartOfSpeech.NOUN,
        displayOrder: Int = 0,
        examples: List<PublicExample> = emptyList()
    ): PublicSense {
        return PublicSense(
            id = id,
            definition = definition,
            engDefinition = engDefinition,
            britishPhonetic = britishPhonetic,
            americanPhonetic = americanPhonetic,
            synonyms = synonyms.map { Spelling(it) },
            antonyms = antonyms.map { Spelling(it) },
            partOfSpeech = partOfSpeech,
            displayOrder = displayOrder,
            examples = examples
        )
    }

    fun createExample(
        id: Long? = null,
        sentence: String = "This is a test sentence.",
        translation: String? = "이것은 테스트 문장입니다.",
        isOfficial: Boolean = false,
        sourceInfo: String? = null,
        displayOrder: Int = 0
    ): PublicExample {
        return PublicExample(
            id = id,
            sentence = sentence,
            translation = translation,
            isOfficial = isOfficial,
            sourceInfo = sourceInfo,
            displayOrder = displayOrder
        )
    }
}
