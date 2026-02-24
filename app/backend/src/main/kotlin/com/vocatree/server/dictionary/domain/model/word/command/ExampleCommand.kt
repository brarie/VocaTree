package com.vocatree.server.dictionary.domain.model.word.command

data class CreateExampleCommand(
    val sentence: String,
    val translation: String? = null,
    val isOfficial: Boolean = false,
    val sourceInfo: String? = null
)

data class UpdateExampleCommand(
    val sentence: String,
    val translation: String? = null,
    val isOfficial: Boolean = false,
    val sourceInfo: String? = null,
)
