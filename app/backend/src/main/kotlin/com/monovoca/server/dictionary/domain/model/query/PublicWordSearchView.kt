package com.monovoca.server.dictionary.domain.model.query

data class PublicWordSearchView(
    val id: Long,
    val spelling: String,
    val slug: String,
    val topDefinitions: List<String>,
)
