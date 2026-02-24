package com.vocatree.server.supportBoard.infrastructure.persistence.repository

import com.vocatree.server.supportBoard.domain.model.SupportCategory
import com.vocatree.server.supportBoard.domain.port.SupportCategoryRepository
import com.vocatree.server.supportBoard.infrastructure.persistence.entity.SupportCategoryEntity
import com.vocatree.server.supportBoard.infrastructure.persistence.entity.SupportCategoryTable
import com.vocatree.server.supportBoard.infrastructure.persistence.entity.SupportPostEntity
import com.vocatree.server.supportBoard.infrastructure.persistence.entity.SupportPostTable
import org.springframework.stereotype.Repository

@Repository
class SupportCategoryRepositoryImpl : SupportCategoryRepository {

    override fun save(supportCategory: SupportCategory): SupportCategory {
        val entity = if (supportCategory.id == null) {
            SupportCategoryEntity.new { applyDomain(supportCategory) }
        } else {
            SupportCategoryEntity.findById(supportCategory.id)?.apply { applyDomain(supportCategory) }
                ?: error("SupportCategory with ID ${supportCategory.id} not found")
        }
        return entity.toDomain()
    }

    override fun findById(categoryId: Long): SupportCategory? =
        SupportCategoryEntity.findById(categoryId)
            ?.toDomain()

    override fun findAllCategories(): List<SupportCategory> =
        SupportCategoryEntity.all()
            .map { it.toDomain() }

    override fun findMainCategories(): List<SupportCategory> =
        SupportCategoryEntity.find { SupportCategoryTable.parentCategoryId.isNull() }
            .map { it.toDomain() }

    override fun findSubCategoriesById(categoryId: Long): List<SupportCategory> =
        SupportCategoryEntity.find { SupportCategoryTable.parentCategoryId eq categoryId }
            .map { it.toDomain() }

    override fun delete(supportCategory: SupportCategory) =
        SupportCategoryEntity.findById(supportCategory.id!!)!!.delete()

    override fun hasSubCategories(categoryId: Long): Boolean =
        SupportCategoryEntity.find { SupportCategoryTable.parentCategoryId eq categoryId }
            .empty().not()

    override fun hasRelatedSupportPosts(categoryId: Long): Boolean =
        SupportPostEntity.find { SupportPostTable.supportCategoryId eq categoryId }
            .empty().not()
}