package com.monovoca.server.supportBoard.infrastructure.persistence

import com.monovoca.server.supportBoard.domain.model.SupportCategory
import com.monovoca.server.supportBoard.domain.port.SupportCategoryRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository

@Repository
class SupportCategoryRepositoryImpl : SupportCategoryRepository {

    override fun save(supportCategory: SupportCategory): SupportCategory = transaction {
        val entity = if (supportCategory.id == null) {
            SupportCategoryEntity.new { applyDomain(supportCategory) }
        } else {
            SupportCategoryEntity.findById(supportCategory.id)?.apply { applyDomain(supportCategory) }
                ?: error("SupportCategory with ID ${supportCategory.id} not found")
        }
        entity.toDomain()
    }

    override fun findById(categoryId: Long): SupportCategory? = transaction {
        SupportCategoryEntity.findById(categoryId)
            ?.toDomain()
    }

    override fun findMainCategories(): List<SupportCategory> = transaction {
        SupportCategoryEntity.find { SupportCategoryTable.parentCategoryId.isNull() }
            .map { it.toDomain() }
    }

    override fun findSubCategoriesById(categoryId: Long): List<SupportCategory> = transaction {
        SupportCategoryEntity.find { SupportCategoryTable.parentCategoryId eq categoryId }
            .map { it.toDomain() }
    }

    override fun delete(supportCategory: SupportCategory) = transaction {
        SupportCategoryEntity.findById(supportCategory.id!!)!!.delete()
    }

    override fun hasSubCategories(categoryId: Long): Boolean = transaction {
        SupportCategoryEntity.find { SupportCategoryTable.parentCategoryId eq categoryId }
            .empty().not()
    }

    override fun hasRelatedSupportPosts(categoryId: Long): Boolean = transaction {
        SupportPostEntity.find { SupportPostTable.supportCategoryId eq categoryId }
            .empty().not()
    }
}