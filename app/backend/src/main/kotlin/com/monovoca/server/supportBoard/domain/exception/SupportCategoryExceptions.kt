package com.monovoca.server.supportBoard.domain.exception

import com.monovoca.server.common.domain.exception.NotFoundException

class SupportCategoryNotFoundException : NotFoundException(
    code = "SUPPORT_CATEGORY_NOT_FOUND",
    message = "해당하는 도움마당 카테고리를 찾을 수 없습니다."
)
