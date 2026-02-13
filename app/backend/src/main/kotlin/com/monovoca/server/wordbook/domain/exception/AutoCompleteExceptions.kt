package com.monovoca.server.wordbook.domain.exception

import com.monovoca.server.common.domain.exception.NotFoundException

class AutoCompleteDataNotFoundException() : NotFoundException(
    code = "AUTO_COMPLETE_DATA_NOT_FOUND",
    message = "자동 완성 데이터를 찾을 수 없습니다.",
)