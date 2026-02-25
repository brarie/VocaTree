package com.vocatree.server.library.infrastructure.persistence.repository

import com.vocatree.server.library.domain.model.SharedWordbookRevision
import com.vocatree.server.library.domain.port.SharedWordbookRevisionRepository
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookEntryTable
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookRevisionEntity
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookRevisionTable
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordSnapshotTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.springframework.stereotype.Repository

@Repository
class SharedWordbookRevisionRepositoryImpl : SharedWordbookRevisionRepository {

    override fun findLatestByWordbookId(sharedWordbookId: Long): SharedWordbookRevision? {
        return SharedWordbookRevisionEntity
            .find(SharedWordbookRevisionTable.sharedWordbookId eq sharedWordbookId)
            .orderBy(SharedWordbookRevisionTable.version to SortOrder.DESC)
            .firstOrNull()
            ?.toDomain()
    }

    override fun findByWordbookIdAndVersion(sharedWordbookId: Long, version: Long): SharedWordbookRevision? {
        return SharedWordbookRevisionEntity
            .find(
                (SharedWordbookRevisionTable.sharedWordbookId eq sharedWordbookId) and
                    (SharedWordbookRevisionTable.version eq version)
            )
            .firstOrNull()
            ?.toDomain()
    }

    override fun save(revision: SharedWordbookRevision, sharedWordbookId: Long): SharedWordbookRevision {
        return SharedWordbookRevisionEntity.new {
            applyDomain(revision, sharedWordbookId)
        }.toDomain()
    }

    override fun saveEntries(revisionId: Long, snapshotIds: List<Long>) {
        SharedWordbookEntryTable.batchInsert(snapshotIds) { snapshotId ->
            this[SharedWordbookEntryTable.revisionId] = EntityID(revisionId, SharedWordbookRevisionTable)
            this[SharedWordbookEntryTable.wordSnapshotId] = EntityID(snapshotId, SharedWordSnapshotTable)
        }
    }
}
