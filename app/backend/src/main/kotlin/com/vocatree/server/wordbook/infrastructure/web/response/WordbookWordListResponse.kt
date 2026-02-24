package com.vocatree.server.wordbook.infrastructure.web.response

import com.vocatree.server.common.infrastructure.web.response.ExampleResponse
import com.vocatree.server.common.infrastructure.web.response.SenseResponse
import com.vocatree.server.wordbook.domain.model.WordbookExample
import com.vocatree.server.wordbook.domain.model.WordbookSense
import com.vocatree.server.wordbook.domain.model.WordbookWord
import com.vocatree.server.wordbook.domain.model.WordbookWordList

data class WordbookWordListResponse(
    val wordbookId: Long,
    val wordList: List<WordbookWordResponse>
) {

    companion object {
        fun from(wordbookWordList: WordbookWordList): WordbookWordListResponse =
            WordbookWordListResponse(
                wordbookId = wordbookWordList.wordbookId,
                wordList = wordbookWordList.wordList.map { WordbookWordResponse.from(it) }
            )
    }

    data class WordbookWordResponse(
        val id: Long,
        val spelling: String?,
        val displayOrder: Int,
        val senses: List<WordbookWordSenseResponse>
    ) {
        companion object {
            fun from(wordbookWord: WordbookWord): WordbookWordResponse =
                WordbookWordResponse(
                    id = wordbookWord.id!!,
                    spelling = wordbookWord.spelling?.value,
                    displayOrder = wordbookWord.displayOrder,
                    senses = wordbookWord.senses.map { WordbookWordSenseResponse.from(it) }
                )
        }
    }

    data class WordbookWordSenseResponse(
        val id: Long,
        val senseResponse: SenseResponse,
        val displayOrder: Int,
        val examples: List<WordbookWordExampleResponse>,
    ) {
        companion object {
            fun from(wordbookWordSense: WordbookSense): WordbookWordSenseResponse =
                WordbookWordSenseResponse(
                    id = wordbookWordSense.id!!,
                    senseResponse = SenseResponse.from(wordbookWordSense.sense),
                    displayOrder = wordbookWordSense.displayOrder,
                    examples = wordbookWordSense.examples.map { WordbookWordExampleResponse.from(it) }
                )
        }
    }

    data class WordbookWordExampleResponse(
        val id: Long,
        val displayOrder: Int,
        val exampleResponse: ExampleResponse,
    ) {
        companion object {
            fun from(wordbookWordExample: WordbookExample): WordbookWordExampleResponse =
                WordbookWordExampleResponse(
                    id = wordbookWordExample.id!!,
                    displayOrder = wordbookWordExample.displayOrder,
                    exampleResponse = ExampleResponse.from(wordbookWordExample.example)
                )
        }
    }
}