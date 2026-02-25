package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.SharedWordSnapshot

interface SharedWordSnapshotRepository {
    fun findOrCreate(snapshot: SharedWordSnapshot): SharedWordSnapshot
    fun findByRevisionId(revisionId: Long): List<SharedWordSnapshot>
}
