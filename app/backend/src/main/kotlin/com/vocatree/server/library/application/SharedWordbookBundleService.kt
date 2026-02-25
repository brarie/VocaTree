package com.vocatree.server.library.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SharedWordbookBundleService {

    @Transactional(readOnly = true)
    fun paginatePublicSharedBundles() {

    }

    @Transactional(readOnly = true)
    fun paginatePublicSharedWordbooksInBundle() {

    }

    @Transactional(readOnly = true)
    fun getSharedWordbookBundleDetail() {

    }

    @Transactional
    fun makeSharedBundle() {

    }

    @Transactional
    fun deleteSharedBundle() {

    }

    @Transactional
    fun updateTrustedLevel() {

    }

    @Transactional
    fun addSharedWordbookToBundle() {

    }

    @Transactional
    fun removeSharedWordbookFromBundle() {

    }
}