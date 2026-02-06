package com.monovoca.server.wordbook.infrastructure.web.request

data class CreateRequest(
    val title: String,
)

data class UpdateBundleTitleRequest(
    val title: String,
)