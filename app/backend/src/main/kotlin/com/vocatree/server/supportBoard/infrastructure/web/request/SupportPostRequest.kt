package com.vocatree.server.supportBoard.infrastructure.web.request

data class CreateSupportPostRequest(
    val title: String,
    val content: String,
    val categoryId: Long
)

data class UpdateSupportPostRequest(
    val title: String,
    val content: String,
    val categoryId: Long
)
