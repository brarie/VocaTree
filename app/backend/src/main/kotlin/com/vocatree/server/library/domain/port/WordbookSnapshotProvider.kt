package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.SharedWordSnapshot

interface WordbookSnapshotProvider {
    fun provide(wordbookId: Long, userId: String): List<SharedWordSnapshot>
}
