package com.vocatree.server.library.infrastructure.persistence.entity

import com.vocatree.server.library.domain.model.SharedWordbookRevision
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object SharedWordbookRevisionTable : LongIdTable("shared_wordbook_revision") {
    val sharedWordbookId = reference("shared_wordbook_id", SharedWordbookTable)
    val version = long("version")
    val versionNote = text("version_note").nullable()
    val revisedAt = datetime("revised_at")

    init {
        uniqueIndex(sharedWordbookId, version)
    }
}

class SharedWordbookRevisionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SharedWordbookRevisionEntity>(SharedWordbookRevisionTable)

    var sharedWordbookId by SharedWordbookRevisionTable.sharedWordbookId
    var version by SharedWordbookRevisionTable.version
    var versionNote by SharedWordbookRevisionTable.versionNote
    var revisedAt by SharedWordbookRevisionTable.revisedAt

    fun toDomain(): SharedWordbookRevision = SharedWordbookRevision(
        id = this.id.value,
        version = this.version,
        versionNote = this.versionNote,
        revisedAt = this.revisedAt,
    )

    fun applyDomain(revision: SharedWordbookRevision, sharedWordbookId: Long) {
        this.sharedWordbookId = EntityID(sharedWordbookId, SharedWordbookTable)
        this.version = revision.version
        this.versionNote = revision.versionNote
        this.revisedAt = revision.revisedAt
    }
}
