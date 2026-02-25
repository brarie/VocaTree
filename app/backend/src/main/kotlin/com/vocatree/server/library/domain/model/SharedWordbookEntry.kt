package com.vocatree.server.library.domain.model

data class SharedWordbookEntry(
    val id: Long? = null,
    val sharedWords: List<SharedWordSnapshot>,
)