package com.monovoca.server.supportBoard.infrastructure.web.response

import com.monovoca.server.supportBoard.domain.model.SupportCategory

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
