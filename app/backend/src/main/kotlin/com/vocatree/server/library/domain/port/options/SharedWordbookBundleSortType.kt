package com.vocatree.server.library.domain.port.options

enum class SharedWordbookBundleSortType(val description: String) {
    CREATED_AT_DESC("최근 생성순"),
    SUBSCRIPTION_COUNT_DESC("구독 많은순"), // TODO: requires subscription tracking
}
