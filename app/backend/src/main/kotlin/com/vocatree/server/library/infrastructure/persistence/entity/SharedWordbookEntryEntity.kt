package com.vocatree.server.library.infrastructure.persistence.entity

import org.jetbrains.exposed.sql.Table

object SharedWordbookEntryTable : Table("shared_wordbook_entry") {
    val revisionId = reference("revision_id", SharedWordbookRevisionTable)
    val wordSnapshotId = reference("word_snapshot_id", SharedWordSnapshotTable)
    override val primaryKey = PrimaryKey(revisionId, wordSnapshotId)
}
