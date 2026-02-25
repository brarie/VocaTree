package com.vocatree.server.library.infrastructure.persistence.repository

import com.vocatree.server.library.domain.model.SharedWordbook
import com.vocatree.server.library.domain.port.SharedWordbookRepository
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookEntity
import org.springframework.stereotype.Repository

@Repository
class SharedWordbookRepositoryImpl : SharedWordbookRepository {

    override fun findById(sharedWordbookId: Long): SharedWordbook? {
        return SharedWordbookEntity.findById(sharedWordbookId)?.toDomain()
    }

    override fun save(sharedWordbook: SharedWordbook): SharedWordbook {
        val entity = if (sharedWordbook.id == null) {
            SharedWordbookEntity.new { applyDomain(sharedWordbook) }
        } else {
            SharedWordbookEntity.findById(sharedWordbook.id)!!.also { it.applyDomain(sharedWordbook) }
        }
        return entity.toDomain()
    }
}
