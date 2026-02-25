package com.vocatree.server.library.infrastructure.persistence.repository

import com.vocatree.server.library.domain.model.SharedWordSnapshot
import com.vocatree.server.library.domain.port.SharedWordSnapshotRepository
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookEntryTable
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordSnapshotEntity
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordSnapshotTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
class SharedWordSnapshotRepositoryImpl : SharedWordSnapshotRepository {

    override fun findOrCreate(snapshot: SharedWordSnapshot): SharedWordSnapshot {
        val checksum = SharedWordSnapshotEntity.computeChecksum(snapshot)
        return SharedWordSnapshotEntity
            .find(SharedWordSnapshotTable.checksum eq checksum)
            .firstOrNull()
            ?.toDomain()
            ?: SharedWordSnapshotEntity.new { applyDomain(snapshot) }.toDomain()
    }

    override fun findByRevisionId(revisionId: Long): List<SharedWordSnapshot> {
        return SharedWordSnapshotEntity.wrapRows(
            (SharedWordbookEntryTable innerJoin SharedWordSnapshotTable)
                .selectAll()
                .where { SharedWordbookEntryTable.revisionId eq revisionId }
        ).toList().map { it.toDomain() }
    }
}
