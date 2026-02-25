package com.vocatree.server.library.domain.port

import com.vocatree.server.library.domain.model.SharedWordbook

interface SharedWordbookRepository {
    fun findById(sharedWordbookId: Long): SharedWordbook?
    fun save(sharedWordbook: SharedWordbook): SharedWordbook
}