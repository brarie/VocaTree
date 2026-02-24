package com.vocatree.server.supportBoard.domain.exception

import com.vocatree.server.common.domain.exception.BadRequestException
import com.vocatree.server.common.domain.exception.NotFoundException

class SupportPostNotFoundException : NotFoundException(
    code = "SUPPORT_POST_NOT_FOUND",
    message = "해당하는 도움마당 게시글을 찾을 수 없습니다."
)

class CannotDeleteCategoryWithSubCategoriesException : BadRequestException(
    code = "CANNOT_DELETE_CATEGORY_WITH_SUBCATEGORIES",
    message = "하위 카테고리가 있는 카테고리는 삭제할 수 없습니다."
)

class CannotDeleteCategoryWithPostsException : BadRequestException(
    code = "CANNOT_DELETE_CATEGORY_WITH_POSTS",
    message = "게시글이 있는 카테고리는 삭제할 수 없습니다."
)