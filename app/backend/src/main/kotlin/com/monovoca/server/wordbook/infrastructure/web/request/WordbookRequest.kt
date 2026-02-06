package com.monovoca.server.wordbook.infrastructure.web.request

data class CreateWordbookRequest(
    val title: String,
)

data class UpdateWordbookTitleRequest(
    val title: String,
)