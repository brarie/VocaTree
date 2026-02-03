package com.monovoca.server.dictionary.domain.model.query

data class PublicWordSimpleView(
    val id: Long,
    val spelling: String,
    val slug: String,
    val topDefinitions: List<String>,
)

data class DictionaryCountView(
    val totalWords: Long,
    val totalSenses: Long,
    val totalExamples: Long,
)