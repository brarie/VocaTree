package com.vocatree.server.library.infrastructure.persistence.entity

import com.vocatree.server.library.domain.model.SharedWordbook
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.user.infrastructure.persistence.entity.UserEntity
import com.vocatree.server.user.infrastructure.persistence.entity.UserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID

object SharedWordbookTable : LongIdTable("shared_wordbook") {
    val creatorId = reference("creator_id", UserTable)
    val title = varchar("title", 200)
    val slug = varchar("slug", 100).nullable().uniqueIndex()
    val description = text("description").nullable()
    val sharedState = enumerationByName("shared_state", 30, SharedState::class)
    val trustedLevel = enumerationByName("trusted_level", 20, TrustedLevel::class).default(TrustedLevel.BASIC)
    val createdAt = datetime("created_at")
}

class SharedWordbookEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SharedWordbookEntity>(SharedWordbookTable)

    var creatorId by SharedWordbookTable.creatorId
    val creator by UserEntity referencedOn SharedWordbookTable.creatorId
    var title by SharedWordbookTable.title
    var slug by SharedWordbookTable.slug
    var description by SharedWordbookTable.description
    var sharedState by SharedWordbookTable.sharedState
    var trustedLevel by SharedWordbookTable.trustedLevel
    var createdAt by SharedWordbookTable.createdAt

    fun toDomain(): SharedWordbook = SharedWordbook(
        id = this.id.value,
        creatorId = this.creatorId.value.toString(),
        title = this.title,
        slug = this.slug,
        description = this.description,
        sharedState = this.sharedState,
        trustedLevel = this.trustedLevel,
        createdAt = this.createdAt,
    )

    fun applyDomain(sharedWordbook: SharedWordbook) {
        this.creatorId = EntityID(UUID.fromString(sharedWordbook.creatorId), UserTable)
        this.title = sharedWordbook.title
        this.slug = sharedWordbook.slug
        this.description = sharedWordbook.description
        this.sharedState = sharedWordbook.sharedState
        this.trustedLevel = sharedWordbook.trustedLevel
        this.createdAt = sharedWordbook.createdAt
    }
}