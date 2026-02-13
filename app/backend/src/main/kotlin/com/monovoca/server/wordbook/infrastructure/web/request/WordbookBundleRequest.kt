package com.monovoca.server.wordbook.infrastructure.web.request

data class CreateBundleRequest(
    val title: String,
)

data class UpdateBundleTitleRequest(
    val title: String,
)