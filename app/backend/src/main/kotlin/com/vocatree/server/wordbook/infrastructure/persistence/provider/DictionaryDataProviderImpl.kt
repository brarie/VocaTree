package com.vocatree.server.wordbook.infrastructure.persistence.provider

import com.vocatree.server.dictionary.domain.port.DictionaryWordRepository
import com.vocatree.server.wordbook.domain.model.command.AutoCompleteDataRequestCommand
import com.vocatree.server.wordbook.domain.model.query.AutoCompleteData
import com.vocatree.server.wordbook.domain.model.query.SenseData
import com.vocatree.server.wordbook.domain.port.DictionaryDataProvider
import org.springframework.stereotype.Component

@Component
class DictionaryDataProviderImpl(
    private val dictionaryWordRepository: DictionaryWordRepository
) : DictionaryDataProvider {

    override fun getAutoCompleteData(requestCommand: AutoCompleteDataRequestCommand): AutoCompleteData? {
        val publicWord = dictionaryWordRepository.findById(requestCommand.dictionaryWordId) ?: return null
        val sensesData = requestCommand.selectedSenses.mapNotNull { selection ->
            val publicSense = publicWord.senses.find { it.id == selection.senseId } ?: return@mapNotNull null
            val examples = publicSense.examples
                .filter { it.id in selection.exampleIds }
                .map { it.example }

            SenseData(
                senseVo = publicSense.sense,
                examples = examples
            )
        }

        return AutoCompleteData(
            spelling = publicWord.spelling,
            senses = sensesData
        )
    }
}
