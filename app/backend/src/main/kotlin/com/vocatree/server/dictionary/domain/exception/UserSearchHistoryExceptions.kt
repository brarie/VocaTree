package com.vocatree.server.dictionary.domain.exception

import com.vocatree.server.common.domain.exception.NotFoundException

class UserSearchHistoryNotFoundException : NotFoundException(
    code = "USER_SEARCH_HISTORY_NOT_FOUND",
    message = "해당 검색 기록을 찾을 수 없습니다."
)
