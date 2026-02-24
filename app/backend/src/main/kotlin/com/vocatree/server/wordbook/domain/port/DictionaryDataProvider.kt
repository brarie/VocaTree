package com.vocatree.server.wordbook.domain.port

import com.vocatree.server.wordbook.domain.model.command.AutoCompleteDataRequestCommand
import com.vocatree.server.wordbook.domain.model.query.AutoCompleteData

interface DictionaryDataProvider {
    fun getAutoCompleteData(requestCommand: AutoCompleteDataRequestCommand): AutoCompleteData?
}