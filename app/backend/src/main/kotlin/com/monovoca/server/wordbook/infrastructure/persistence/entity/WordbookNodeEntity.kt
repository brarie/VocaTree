package com.monovoca.server.wordbook.infrastructure.persistence.entity

import com.monovoca.server.user.infrastructure.persistence.entity.UserTable
import com.monovoca.server.wordbook.domain.model.Wordbook
import com.monovoca.server.wordbook.domain.model.WordbookBundle
import com.monovoca.server.wordbook.domain.model.WordbookNode
import com.monovoca.server.wordbook.domain.model.enumerated.NodeType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object WordbookNodeTable : LongIdTable("wordbook_node") {
    val ownerId = reference("owner_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val parentId = reference("parent_id", WordbookNodeTable, onDelete = ReferenceOption.CASCADE).nullable()
    val nodeType = enumerationByName("node_type", 20, NodeType::class)
    val isStarred = bool("is_starred").default(false)
    val title = varchar("title", 200)
    val createdAt = datetime("created_at")
}

class WordbookNodeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<WordbookNodeEntity>(WordbookNodeTable)

    var ownerId by WordbookNodeTable.ownerId
    var parentId by WordbookNodeTable.parentId
    var nodeType by WordbookNodeTable.nodeType
    var isStarred by WordbookNodeTable.isStarred
    var title by WordbookNodeTable.title
    var createdAt by WordbookNodeTable.createdAt
    val settings by WordbookColumnSettingEntity referrersOn WordbookColumnSettingTable.wordbookId

    fun toDomain(): WordbookNode =
        when (nodeType) {
            NodeType.SINGLE ->
                Wordbook(
                    id = this.id.value,
                    title = this.title,
                    ownerId = this.ownerId.value.toString(),
                    isStarred = this.isStarred,
                    bundleId = this.parentId?.value,
                    createdAt = this.createdAt,
                    columns = settings.associateBy({ it.columnType }) { it.toDomain() } // todo
                )

            NodeType.BUNDLE ->
                WordbookBundle(
                    id = this.id.value,
                    title = this.title,
                    ownerId = this.ownerId.value.toString(),
                    isStarred = this.isStarred,
                    createdAt = this.createdAt,
                )
        }

    fun applyDomain(wordbookNode: WordbookNode) {
        this.ownerId = EntityID(UUID.fromString(wordbookNode.ownerId), UserTable)
        this.parentId = wordbookNode.let {
            when (it) {
                is Wordbook -> it.bundleId?.let { bundleId -> EntityID(bundleId, WordbookNodeTable) }
                is WordbookBundle -> null
            }
        }
        this.isStarred = wordbookNode.isStarred
        this.nodeType = wordbookNode.nodeType
        this.title = wordbookNode.title
        this.createdAt = wordbookNode.createdAt
    }
}