package com.monovoca.server.supportBoard.fixture

import com.monovoca.server.supportBoard.domain.model.SupportCategory

object SupportCategoryFixture {

    fun createMainCategory(
        id: Long? = null,
        categoryName: String = "대분류 카테고리",
    ): SupportCategory {
        return SupportCategory(
            id = id,
            categoryName = categoryName,
            parentCategory = null,
        )
    }

    fun createSubCategory(
        id: Long? = null,
        categoryName: String = "중분류 카테고리",
        parentCategory: SupportCategory = createMainCategory(id = 1L),
    ): SupportCategory {
        return SupportCategory(
            id = id,
            categoryName = categoryName,
            parentCategory = parentCategory,
        )
    }
}
