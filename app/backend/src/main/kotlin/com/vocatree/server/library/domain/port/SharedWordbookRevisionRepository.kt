package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.SharedWordbookRevision

interface SharedWordbookRevisionRepository {
    fun findLatestByWordbookId(sharedWordbookId: Long): SharedWordbookRevision?
    fun findByWordbookIdAndVersion(sharedWordbookId: Long, version: Long): SharedWordbookRevision?
    fun save(revision: SharedWordbookRevision, sharedWordbookId: Long): SharedWordbookRevision
    fun saveEntries(revisionId: Long, snapshotIds: List<Long>)
}