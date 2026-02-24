package com.vocatree.server.supportBoard.infrastructure.web.response

import com.vocatree.server.supportBoard.domain.model.SupportCategory

data class SupportCategoryResponse(
    val id: Long,
    val categoryName: String,
    val parentCategoryId: Long?
) {
    companion object {
        fun from(category: SupportCategory) = SupportCategoryResponse(
            id = category.id!!,
            categoryName = category.categoryName,
            parentCategoryId = category.parentCategory?.id
        )
    }
}
