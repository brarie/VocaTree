package com.vocatree.server.library.infrastructure.persistence.entity

import com.vocatree.server.library.domain.model.SharedWordbookBundle
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.user.infrastructure.persistence.entity.UserEntity
import com.vocatree.server.user.infrastructure.persistence.entity.UserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID

object SharedWordbookBundleTable : LongIdTable("shared_wordbook_bundle") {
    val creatorId = reference("creator_id", UserTable)
    val title = varchar("title", 200)
    val slug = varchar("slug", 100).nullable().uniqueIndex()
    val description = text("description").nullable()
    val sharedState = enumerationByName("shared_state", 30, SharedState::class)
    val trustedLevel = enumerationByName("trusted_level", 20, TrustedLevel::class).default(TrustedLevel.BASIC)
    val createdAt = datetime("created_at")
}

// Bundle에 포함된 단어장 N:M join table
object SharedWordbookBundleItemTable : Table("shared_wordbook_bundle_item") {
    val bundleId = reference("bundle_id", SharedWordbookBundleTable, onDelete = ReferenceOption.CASCADE)
    val sharedWordbookId = reference("shared_wordbook_id", SharedWordbookTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(bundleId, sharedWordbookId)
}

class SharedWordbookBundleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SharedWordbookBundleEntity>(SharedWordbookBundleTable)

    var creatorId by SharedWordbookBundleTable.creatorId
    val creator by UserEntity referencedOn SharedWordbookBundleTable.creatorId
    var title by SharedWordbookBundleTable.title
    var slug by SharedWordbookBundleTable.slug
    var description by SharedWordbookBundleTable.description
    var sharedState by SharedWordbookBundleTable.sharedState
    var trustedLevel by SharedWordbookBundleTable.trustedLevel
    var createdAt by SharedWordbookBundleTable.createdAt

//    val sharedWordbooks by SharedWordbookEntity via SharedWordbookBundleItemTable

    fun toDomain(): SharedWordbookBundle = SharedWordbookBundle(
        id = this.id.value,
        creatorId = this.creatorId.value.toString(),
        title = this.title,
        slug = this.slug,
        description = this.description,
        sharedState = this.sharedState,
        trustedLevel = this.trustedLevel,
        createdAt = this.createdAt,
    )

    fun applyDomain(bundle: SharedWordbookBundle) {
        this.creatorId = EntityID(UUID.fromString(bundle.creatorId), UserTable)
        this.title = bundle.title
        this.slug = bundle.slug
        this.description = bundle.description
        this.sharedState = bundle.sharedState
        this.trustedLevel = bundle.trustedLevel
        this.createdAt = bundle.createdAt
    }
}
