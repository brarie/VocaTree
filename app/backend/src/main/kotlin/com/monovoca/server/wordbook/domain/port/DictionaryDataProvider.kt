package com.monovoca.server.wordbook.domain.port

import com.monovoca.server.wordbook.domain.model.command.AutoCompleteDataRequestCommand
import com.monovoca.server.wordbook.domain.model.query.AutoCompleteData

interface DictionaryDataProvider {
    fun getAutoCompleteData(requestCommand: AutoCompleteDataRequestCommand): AutoCompleteData?
}