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

    /**
     * 기본 실행 방법
     * 담겨있는 단어장들을 모두 일단 구독함.
     * 구독한 단어장 모음을 구독함에 뜨게 함
     * 얘는 항상 sync됨
     */
    @Transactional
    fun subscribeAll() {

    }
}