package com.monovoca.server.supportBoard.infrastructure.persistence

import com.monovoca.server.common.domain.model.query.SimpleUserView
import com.monovoca.server.supportBoard.domain.model.SupportPost
import com.monovoca.server.supportBoard.domain.model.query.SupportPostView
import com.monovoca.server.user.infrastructure.persistence.UserEntity
import com.monovoca.server.user.infrastructure.persistence.UserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID

object SupportPostTable : LongIdTable("support_posts") {
    val title = varchar("title", 255)
    val content = text("content")
    val createdAt = datetime("created_at")

    val authorId = reference("author_id", UserTable.id)
    val supportCategoryId = reference("support_category_id", SupportCategoryTable.id)
}

class SupportPostEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SupportPostEntity>(SupportPostTable)

    var title by SupportPostTable.title
    var content by SupportPostTable.content
    var createdAt by SupportPostTable.createdAt

    var author by UserEntity referencedOn SupportPostTable.authorId
    var authorId by SupportPostTable.authorId

    var supportCategory by SupportCategoryEntity referencedOn SupportPostTable.supportCategoryId
    var supportCategoryId by SupportPostTable.supportCategoryId

    fun toDomain(): SupportPost = SupportPost(
        id = this.id.value,
        title = this.title,
        content = this.content,
        authorId = this.authorId.value.toString(),
        createdAt = this.createdAt,
        supportCategory = this.supportCategory.toDomain()
    )

    fun applyDomain(supportPost: SupportPost) {
        title = supportPost.title
        content = supportPost.content
        createdAt = supportPost.createdAt

        val newAuthorId = UUID.fromString(supportPost.authorId)
        author = UserEntity[newAuthorId]
        authorId = EntityID(newAuthorId, UserTable)

        val newSupportCategoryId = supportPost.supportCategory.id!!
        supportCategory = SupportCategoryEntity[newSupportCategoryId]
        supportCategoryId = EntityID(newSupportCategoryId, SupportCategoryTable)
    }
}

fun SupportPostEntity.toSupportPostView() = SupportPostView(
    id = this.id.value,
    title = this.title,
    content = this.content,
    author = SimpleUserView(
        id = this.author.id.value.toString(),
        nickname = this.author.nickname
    ),
    supportCategoryId = this.supportCategory.id.value,
    supportCategoryName = this.supportCategory.categoryName,
    createdAt = this.createdAt,
)