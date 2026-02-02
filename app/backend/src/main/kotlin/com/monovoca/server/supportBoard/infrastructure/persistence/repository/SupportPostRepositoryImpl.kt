package com.monovoca.server.supportBoard.infrastructure.persistence.repository

import com.monovoca.server.supportBoard.domain.model.SupportPost
import com.monovoca.server.supportBoard.domain.model.query.SupportPostView
import com.monovoca.server.supportBoard.domain.port.SupportPostRepository
import com.monovoca.server.supportBoard.infrastructure.persistence.entity.SupportCategoryTable
import com.monovoca.server.supportBoard.infrastructure.persistence.entity.SupportPostEntity
import com.monovoca.server.supportBoard.infrastructure.persistence.entity.SupportPostTable
import com.monovoca.server.supportBoard.infrastructure.persistence.entity.toSupportPostView
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class SupportPostRepositoryImpl : SupportPostRepository {

    override fun save(supportPost: SupportPost): SupportPost = transaction {
        val entity = if (supportPost.id == null) {
            SupportPostEntity.new { applyDomain(supportPost) }
        } else {
            SupportPostEntity.findById(supportPost.id)?.apply { applyDomain(supportPost) }
                ?: error("SupportPost with ID ${supportPost.id} not found")
        }
        entity.toDomain()
    }

    override fun delete(supportPost: SupportPost) = transaction {
        SupportPostEntity.findById(supportPost.id!!)!!.delete()
    }

    override fun findById(supportPostId: Long): SupportPost? = transaction {
        SupportPostEntity.findById(supportPostId)?.toDomain()
    }

    override fun paginateViewsByCategoryTree(
        categoryId: Long,
        pageable: Pageable
    ): Page<SupportPostView> = transaction {
        val categoryIds = SupportCategoryTable
            .select(SupportCategoryTable.id)
            .where { (SupportCategoryTable.id eq categoryId) or (SupportCategoryTable.parentCategoryId eq categoryId) }
            .map { it[SupportCategoryTable.id].value }

        val total = SupportPostTable
            .selectAll().where { SupportPostTable.supportCategoryId inList categoryIds }
            .count()

        val content = SupportPostEntity
            .find { SupportPostTable.supportCategoryId inList categoryIds }
            .orderBy(
                SupportPostTable.createdAt to SortOrder.DESC,
                SupportPostTable.id to SortOrder.DESC
            )
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .with(SupportPostEntity::author, SupportPostEntity::supportCategory)
            .map { it.toSupportPostView() }

        PageImpl(content, pageable, total)
    }
}