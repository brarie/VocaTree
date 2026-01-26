package com.monovoca.server.supportBoard.domain.port

import com.monovoca.server.supportBoard.domain.model.SupportCategory

interface SupportCategoryRepository {

    fun save(supportCategory: SupportCategory): SupportCategory
    fun findById(categoryId: Long): SupportCategory?
    fun findMainCategories(): List<SupportCategory>
    fun findSubCategoriesById(categoryId: Long): List<SupportCategory>
    fun findAllCategories(): List<SupportCategory>
    fun delete(supportCategory: SupportCategory)
    fun hasSubCategories(categoryId: Long): Boolean
    fun hasRelatedSupportPosts(categoryId: Long): Boolean
}