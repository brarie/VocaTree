package com.monovoca.server.supportBoard.domain.model

/**
 * parentCategory가 null 이면 대분류(main category), null이 아니면 중분류(sub-category)
 * Note: supportCategory를 지우기 위해서는 해당 카테고리에 속한 supportBoard가 없어야 한다.
 */
data class SupportCategory(
    val id: Long? = null,
    val categoryName: String,
    val parentCategory: SupportCategory?,
) {

    init {
        if (parentCategory != null) {
            require(parentCategory.isMainCategory()) { "중분류 카테고리는 대분류 카테고리를 가져야 합니다." }
        }
    }

    fun isMainCategory(): Boolean = parentCategory == null
    fun isSubCategory(): Boolean = parentCategory != null

    companion object {
        fun create(
            categoryName: String,
            parentCategory: SupportCategory?,
        ): SupportCategory = SupportCategory(
            categoryName = categoryName,
            parentCategory = parentCategory,
        )

    }

    fun update(toCategoryName: String): SupportCategory {
        return this.copy(
            categoryName = toCategoryName
        )
    }
}