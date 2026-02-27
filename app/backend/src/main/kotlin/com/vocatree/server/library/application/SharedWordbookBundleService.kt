package com.vocatree.server.library.application

import com.vocatree.server.common.domain.port.holder.LocalDateTimeHolder
import com.vocatree.server.library.domain.model.SharedWordbookBundle
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.exception.SharedWordbookBundleNotFoundException
import com.vocatree.server.library.domain.model.exception.SharedWordbookNotFoundException
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleListView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.port.SharedWordbookBundleRepository
import com.vocatree.server.library.domain.port.SharedWordbookRepository
import com.vocatree.server.library.domain.port.options.SharedWordbookBundleSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SharedWordbookBundleService(
    private val sharedWordbookBundleRepository: SharedWordbookBundleRepository,
    private val sharedWordbookRepository: SharedWordbookRepository,
    private val localDateTimeHolder: LocalDateTimeHolder,
) {

    @Transactional(readOnly = true)
    fun paginatePublicSharedBundles(
        titlePrefix: String?,
        trustedLevels: List<TrustedLevel>,
        sortType: SharedWordbookBundleSortType,
        pageable: Pageable,
    ): Page<SharedWordbookBundleListView> =
        sharedWordbookBundleRepository.paginatePublicViews(titlePrefix, trustedLevels, sortType, pageable)

    @Transactional(readOnly = true)
    fun getSharedWordbookBundleDetail(bundleId: Long): SharedWordbookBundleDetailView =
        sharedWordbookBundleRepository.findDetailView(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

    @Transactional(readOnly = true)
    fun paginatePublicSharedWordbooksInBundle(bundleId: Long, pageable: Pageable): Page<SharedWordbookListView> {
        sharedWordbookBundleRepository.findById(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

        return sharedWordbookBundleRepository.paginateWordbooksInBundle(bundleId, pageable)
    }

    @Transactional(readOnly = true)
    fun getMySharedBundles(userId: String, pageable: Pageable): Page<SharedWordbookBundleListView> =
        sharedWordbookBundleRepository.paginateByCreatorId(userId, pageable)

    @Transactional
    fun makeSharedBundle(userId: String, title: String, description: String?): SharedWordbookBundle {
        val temporarySaved = sharedWordbookBundleRepository.save(
            SharedWordbookBundle(
                creatorId = userId,
                title = title,
                description = description,
                slug = null,
                createdAt = localDateTimeHolder.now(),
            )
        )
        return sharedWordbookBundleRepository.save(temporarySaved.updateSlug())
    }

    @Transactional
    fun updateSharedBundleMetadata(
        userId: String,
        bundleId: Long,
        title: String,
        description: String?,
    ): SharedWordbookBundle {
        val bundle = sharedWordbookBundleRepository.findById(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

        bundle.validateOwner(userId)

        return sharedWordbookBundleRepository.save(bundle.updateMetadata(title, description))
    }

    @Transactional
    fun deleteSharedBundle(userId: String, bundleId: Long): SharedWordbookBundle {
        val bundle = sharedWordbookBundleRepository.findById(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

        bundle.validateOwner(userId)

        return sharedWordbookBundleRepository.save(bundle.delete())
    }

    @Transactional
    fun addSharedWordbookToBundle(userId: String, bundleId: Long, sharedWordbookId: Long) {
        val bundle = sharedWordbookBundleRepository.findById(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

        bundle.validateOwner(userId)

        sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        sharedWordbookBundleRepository.addWordbook(bundleId, sharedWordbookId)
    }

    @Transactional
    fun removeSharedWordbookFromBundle(userId: String, bundleId: Long, sharedWordbookId: Long) {
        val bundle = sharedWordbookBundleRepository.findById(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

        bundle.validateOwner(userId)

        sharedWordbookBundleRepository.removeWordbook(bundleId, sharedWordbookId)
    }

    @Transactional
    fun banSharedBundle(bundleId: Long): SharedWordbookBundle {
        val bundle = sharedWordbookBundleRepository.findById(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

        return sharedWordbookBundleRepository.save(bundle.ban())
    }

    @Transactional
    fun updateTrustedLevel(bundleId: Long, trustedLevel: TrustedLevel): SharedWordbookBundle {
        val bundle = sharedWordbookBundleRepository.findById(bundleId)
            ?: throw SharedWordbookBundleNotFoundException()

        return sharedWordbookBundleRepository.save(bundle.updateTrustedLevel(trustedLevel))
    }
}
