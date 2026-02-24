package com.vocatree.server.wordbook.domain.port.options

enum class WordbookNodeSortType(val description: String) {
    TITLE_ASC("제목 오름차순"),
    TITLE_DESC("제목 내림차순"),
    CREATED_AT_ASC("생성일 오름차순"),
    CREATED_AT_DESC("생성일 내림차순"),
}