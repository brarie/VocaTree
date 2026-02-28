package com.vocatree.server.library.application

import com.vocatree.server.common.domain.port.holder.LocalDateTimeHolder
import com.vocatree.server.library.domain.model.SharedWordbook
import com.vocatree.server.library.domain.model.SharedWordbookRevision
import com.vocatree.server.library.domain.model.SharedWordSnapshot
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.exception.SharedWordbookNotFoundException
import com.vocatree.server.library.domain.model.exception.SharedWordbookRevisionNotFoundException
import com.vocatree.server.library.domain.model.query.SharedWordbookDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.model.query.SharedWordbookVersionView
import com.vocatree.server.library.domain.port.options.SharedWordbookSortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import com.vocatree.server.library.domain.port.SharedWordSnapshotRepository
import com.vocatree.server.library.domain.port.SharedWordbookRepository
import com.vocatree.server.library.domain.port.SharedWordbookRevisionRepository
import com.vocatree.server.library.domain.port.WordbookSnapshotProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SharedWordbookService(
    private val sharedWordbookRepository: SharedWordbookRepository,
    private val sharedWordbookRevisionRepository: SharedWordbookRevisionRepository,
    private val sharedWordSnapshotRepository: SharedWordSnapshotRepository,
    private val wordbookSnapshotProvider: WordbookSnapshotProvider,
    private val localDateTimeHolder: LocalDateTimeHolder,
) {

    @Transactional(readOnly = true)
    fun getMySharedWordbooks(userId: String, pageable: Pageable): Page<SharedWordbookListView> =
        sharedWordbookRepository.paginateByCreatorId(userId, pageable)

    @Transactional(readOnly = true)
    fun paginatePublicSharedWordbooks(
        titlePrefix: String?,
        trustedLevels: List<TrustedLevel>,
        sortType: SharedWordbookSortType,
        pageable: Pageable,
    ): Page<SharedWordbookListView> =
        sharedWordbookRepository.paginatePublicViews(titlePrefix, trustedLevels, sortType, pageable)

    @Transactional(readOnly = true)
    fun paginateAllSharedWordbooks(
        titlePrefix: String?,
        pageable: Pageable,
    ): Page<SharedWordbookListView> =
        sharedWordbookRepository.paginateAllViews(titlePrefix, pageable)

    @Transactional(readOnly = true)
    fun getSharedWordbookDetail(sharedWordbookId: Long): SharedWordbookDetailView {
        val sharedWordbook = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        sharedWordbook.validatePublic()

        return sharedWordbookRepository.findDetailView(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()
    }

    @Transactional(readOnly = true)
    fun getSharedWordbookVersions(sharedWordbookId: Long, pageable: Pageable): Page<SharedWordbookVersionView> {
        val sharedWordbook = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        sharedWordbook.validatePublic()

        return sharedWordbookRevisionRepository.findVersionViews(sharedWordbookId, pageable)
    }

    @Transactional(readOnly = true)
    fun getSharedWordsByVersion(sharedWordbookId: Long, version: Long): List<SharedWordSnapshot> {
        val sharedWordbook = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        sharedWordbook.validatePublic()

        val revision = sharedWordbookRevisionRepository.findByWordbookIdAndVersion(sharedWordbookId, version)
            ?: throw SharedWordbookRevisionNotFoundException()

        return sharedWordSnapshotRepository.findByRevisionId(revision.id!!)
    }

    @Transactional
    fun shareOwnWordbook(
        userId: String,
        title: String,
        description: String?,
        myWordbookId: Long,
        versionNote: String,
    ): SharedWordbook {
        val temporarySaved = sharedWordbookRepository.save(
            SharedWordbook(
                creatorId = userId,
                title = title,
                description = description,
                slug = null,
                createdAt = localDateTimeHolder.now(),
            )
        )
        val saved = sharedWordbookRepository.save(temporarySaved.updateSlug())

        val snapshotIds = wordbookSnapshotProvider
            .provide(myWordbookId, userId)
            .map { snapshot -> sharedWordSnapshotRepository.findOrCreate(snapshot).id!! }

        val savedRevision = sharedWordbookRevisionRepository.save(
            revision = SharedWordbookRevision(
                version = 1L,
                versionNote = versionNote,
                revisedAt = localDateTimeHolder.now(),
            ),
            sharedWordbookId = saved.id!!,
        )

        sharedWordbookRevisionRepository.saveEntries(savedRevision.id!!, snapshotIds)

        return saved
    }

    @Transactional
    fun updateSharedWordbookMetadata(
        userId: String,
        sharedWordbookId: Long,
        title: String,
        description: String?,
    ): SharedWordbook {
        val shared = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        shared.validateOwner(userId)

        val updated = shared.updateMetadata(title, description)
        return sharedWordbookRepository.save(updated)
    }

    @Transactional
    fun makeNewVersion(
        userId: String,
        sharedWordbookId: Long,
        myWordbookId: Long,
        versionNote: String,
    ): SharedWordbookRevision {
        val shared = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        shared.validateOwner(userId)

        val snapshotIds = wordbookSnapshotProvider
            .provide(myWordbookId, userId)
            .map { snapshot -> sharedWordSnapshotRepository.findOrCreate(snapshot).id!! }

        val latestVersion = sharedWordbookRevisionRepository
            .findLatestByWordbookId(sharedWordbookId)?.version ?: 0L

        val savedRevision = sharedWordbookRevisionRepository.save(
            revision = SharedWordbookRevision(
                version = latestVersion + 1,
                versionNote = versionNote,
                revisedAt = localDateTimeHolder.now(),
            ),
            sharedWordbookId = sharedWordbookId,
        )

        sharedWordbookRevisionRepository.saveEntries(savedRevision.id!!, snapshotIds)

        return savedRevision
    }

    @Transactional
    fun deleteMySharedWordbook(userId: String, sharedWordbookId: Long): SharedWordbook {
        val shared = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        shared.validateOwner(userId)

        return sharedWordbookRepository.save(shared.delete())
    }

    @Transactional
    fun banSharedWordbook(sharedWordbookId: Long): SharedWordbook {
        val shared = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        return sharedWordbookRepository.save(shared.ban())
    }

    @Transactional
    fun unbanSharedWordbook(sharedWordbookId: Long): SharedWordbook {
        val shared = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        return sharedWordbookRepository.save(shared.unban())
    }

    @Transactional
    fun updateTrustedLevel(sharedWordbookId: Long, trustedLevel: TrustedLevel): SharedWordbook {
        val shared = sharedWordbookRepository.findById(sharedWordbookId)
            ?: throw SharedWordbookNotFoundException()

        return sharedWordbookRepository.save(shared.updateTrustedLevel(trustedLevel))
    }
}