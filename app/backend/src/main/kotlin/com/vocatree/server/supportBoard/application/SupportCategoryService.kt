package com.vocatree.server.supportBoard.application

import com.vocatree.server.supportBoard.domain.exception.CannotDeleteCategoryWithPostsException
import com.vocatree.server.supportBoard.domain.exception.CannotDeleteCategoryWithSubCategoriesException
import com.vocatree.server.supportBoard.domain.exception.SupportCategoryNotFoundException
import com.vocatree.server.supportBoard.domain.model.SupportCategory
import com.vocatree.server.supportBoard.domain.port.SupportCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupportCategoryService(
    private val supportCategoryRepository: SupportCategoryRepository
) {
    @Transactional(readOnly = true)
    fun findAllCategories(): List<SupportCategory> = supportCategoryRepository.findAllCategories()

    @Transactional(readOnly = true)
    fun findMainCategories(): List<SupportCategory> = supportCategoryRepository.findMainCategories()

    @Transactional(readOnly = true)
    fun findSubCategoriesById(categoryId: Long): List<SupportCategory> =
        supportCategoryRepository.findSubCategoriesById(categoryId)

    @Transactional
    fun createMainCategory(categoryName: String): SupportCategory {
        val category = SupportCategory.create(categoryName, null).let {
            supportCategoryRepository.save(it)
        }
        return category
    }

    @Transactional
    fun createSubCategory(categoryName: String, parentCategoryId: Long): SupportCategory {
        val parentCategory = supportCategoryRepository.findById(parentCategoryId)
            ?: throw SupportCategoryNotFoundException()

        val category = SupportCategory.create(categoryName, parentCategory).let {
            supportCategoryRepository.save(it)
        }

        return category
    }

    @Transactional
    fun update(categoryId: Long, toCategoryName: String): SupportCategory {
        val category = supportCategoryRepository.findById(categoryId)
            ?: throw SupportCategoryNotFoundException()

        val updatedCategory = category.update(toCategoryName).let {
            supportCategoryRepository.save(it)
        }

        return updatedCategory
    }

    @Transactional
    fun delete(categoryId: Long) {
        val category = supportCategoryRepository.findById(categoryId)
            ?: throw SupportCategoryNotFoundException()

        if (supportCategoryRepository.hasSubCategories(categoryId))
            throw CannotDeleteCategoryWithSubCategoriesException()

        if (supportCategoryRepository.hasRelatedSupportPosts(categoryId))
            throw CannotDeleteCategoryWithPostsException()

        supportCategoryRepository.delete(category)
    }
}