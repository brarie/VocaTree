package com.vocatree.server.wordbook.domain.port

import com.vocatree.server.wordbook.domain.model.Wordbook

interface WordbookRepository {

    fun findById(id: Long): Wordbook?
    fun save(wordbook: Wordbook): Wordbook
    fun delete(wordbook: Wordbook)
    fun existsByBundleId(bundleId: Long): Boolean
}