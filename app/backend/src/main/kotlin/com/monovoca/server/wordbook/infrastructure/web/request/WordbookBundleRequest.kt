package com.monovoca.server.wordbook.infrastructure.web.request

/* TODO: 실수로 WordbookRequest 에 있는 걸 썼기 때문에 추후 refactoring  예정 */

data class CreateRequest(
    val title: String,
)

data class UpdateBundleTitleRequest(
    val title: String,
)