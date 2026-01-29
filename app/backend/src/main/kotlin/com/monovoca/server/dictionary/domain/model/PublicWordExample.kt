package com.monovoca.server.dictionary.domain.model

data class PublicWordExample(
    val id: Long?,
    val sentence: String,
    val translation: String?,
    val isOfficial: Boolean = false,
    val source: String?
)
