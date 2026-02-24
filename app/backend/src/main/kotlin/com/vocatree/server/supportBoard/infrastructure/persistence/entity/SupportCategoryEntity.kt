package com.vocatree.server.supportBoard.infrastructure.persistence.entity

import com.vocatree.server.supportBoard.domain.model.SupportCategory
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object SupportCategoryTable : LongIdTable("support_categories") {
    val categoryName = varchar("category_name", 255)
    val parentCategoryId = optReference("parent_category_id", SupportCategoryTable)
}

class SupportCategoryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SupportCategoryEntity>(SupportCategoryTable)

    var categoryName by SupportCategoryTable.categoryName

    var parentCategoryId by SupportCategoryTable.parentCategoryId
    var parentCategory by SupportCategoryEntity optionalReferencedOn SupportCategoryTable.parentCategoryId

    fun applyDomain(supportCategory: SupportCategory) {
        categoryName = supportCategory.categoryName

        val newParentCategoryId = supportCategory.parentCategory?.id
        parentCategoryId = newParentCategoryId?.let {
            EntityID(newParentCategoryId, SupportCategoryTable)
        }
        parentCategory = newParentCategoryId?.let {
            SupportCategoryEntity[it]
        }
    }

    fun toDomain(): SupportCategory = SupportCategory(
        id = this.id.value,
        categoryName = this.categoryName,
        parentCategory = this.parentCategory?.toDomain()
    )
}