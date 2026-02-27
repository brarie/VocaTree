package com.vocatree.server.library.infrastructure.persistence.repository

import com.vocatree.server.common.domain.model.query.SimpleUserView
import com.vocatree.server.library.domain.model.SharedWordbookBundle
import com.vocatree.server.library.domain.model.enumerated.SharedState
import com.vocatree.server.library.domain.model.enumerated.TrustedLevel
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleDetailView
import com.vocatree.server.library.domain.model.query.SharedWordbookBundleListView
import com.vocatree.server.library.domain.model.query.SharedWordbookListView
import com.vocatree.server.library.domain.port.SharedWordbookBundleRepository
import com.vocatree.server.library.domain.port.options.SharedWordbookBundleSortType
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookBundleEntity
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookBundleItemTable
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookBundleTable
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookRevisionTable
import com.vocatree.server.library.infrastructure.persistence.entity.SharedWordbookTable
import com.vocatree.server.user.infrastructure.persistence.entity.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class SharedWordbookBundleRepositoryImpl : SharedWordbookBundleRepository {

    override fun findById(bundleId: Long): SharedWordbookBundle? =
        SharedWordbookBundleEntity.findById(bundleId)?.toDomain()

    override fun save(bundle: SharedWordbookBundle): SharedWordbookBundle {
        val entity = if (bundle.id == null) {
            SharedWordbookBundleEntity.new { applyDomain(bundle) }
        } else {
            SharedWordbookBundleEntity.findById(bundle.id)!!.also { it.applyDomain(bundle) }
        }
        return entity.toDomain()
    }

    override fun paginatePublicViews(
        titlePrefix: String?,
        trustedLevels: List<TrustedLevel>,
        sortType: SharedWordbookBundleSortType,
        pageable: Pageable,
    ): Page<SharedWordbookBundleListView> {
        val conditionBuilder: SqlExpressionBuilder.() -> Op<Boolean> = {
            val conditions = mutableListOf<Op<Boolean>>()
            conditions.add(SharedWordbookBundleTable.sharedState eq SharedState.PUBLIC)
            if (!titlePrefix.isNullOrBlank()) {
                conditions.add(SharedWordbookBundleTable.title like "$titlePrefix%")
            }
            if (trustedLevels.isNotEmpty()) {
                conditions.add(SharedWordbookBundleTable.trustedLevel inList trustedLevels)
            }
            conditions.compoundAnd()
        }

        val total = SharedWordbookBundleTable.selectAll().where(conditionBuilder).count()

        val sortOrder = when (sortType) {
            SharedWordbookBundleSortType.CREATED_AT_DESC -> SharedWordbookBundleTable.createdAt to SortOrder.DESC
            SharedWordbookBundleSortType.SUBSCRIPTION_COUNT_DESC -> SharedWordbookBundleTable.createdAt to SortOrder.DESC
        }

        val wordbookCountAlias = SharedWordbookBundleItemTable.sharedWordbookId.count().alias("wordbook_count")

        val trustedLevelOrder = Case()
            .When(SharedWordbookBundleTable.trustedLevel eq TrustedLevel.OFFICIAL, intLiteral(1))
            .When(SharedWordbookBundleTable.trustedLevel eq TrustedLevel.VERIFIED, intLiteral(2))
            .Else(intLiteral(3))

        val content = SharedWordbookBundleTable
            .join(UserTable, JoinType.INNER, SharedWordbookBundleTable.creatorId, UserTable.id)
            .join(SharedWordbookBundleItemTable, JoinType.LEFT, SharedWordbookBundleTable.id, SharedWordbookBundleItemTable.bundleId)
            .select(
                SharedWordbookBundleTable.id,
                SharedWordbookBundleTable.title,
                SharedWordbookBundleTable.slug,
                SharedWordbookBundleTable.sharedState,
                SharedWordbookBundleTable.trustedLevel,
                SharedWordbookBundleTable.createdAt,
                UserTable.id,
                UserTable.nickname,
                wordbookCountAlias,
            )
            .where(conditionBuilder)
            .groupBy(SharedWordbookBundleTable.id, UserTable.id)
            .orderBy(trustedLevelOrder to SortOrder.ASC, sortOrder, SharedWordbookBundleTable.id to SortOrder.DESC)
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .map { row ->
                SharedWordbookBundleListView(
                    id = row[SharedWordbookBundleTable.id].value,
                    title = row[SharedWordbookBundleTable.title],
                    slug = row[SharedWordbookBundleTable.slug]!!,
                    sharedState = row[SharedWordbookBundleTable.sharedState],
                    trustedLevel = row[SharedWordbookBundleTable.trustedLevel],
                    creator = SimpleUserView(
                        id = row[UserTable.id].value.toString(),
                        nickname = row[UserTable.nickname],
                    ),
                    wordbookCount = row[wordbookCountAlias].toInt(),
                    createdAt = row[SharedWordbookBundleTable.createdAt],
                )
            }

        return PageImpl(content, pageable, total)
    }

    override fun findDetailView(bundleId: Long): SharedWordbookBundleDetailView? {
        val wordbookCountAlias = SharedWordbookBundleItemTable.sharedWordbookId.count().alias("wordbook_count")

        return SharedWordbookBundleTable
            .join(UserTable, JoinType.INNER, SharedWordbookBundleTable.creatorId, UserTable.id)
            .join(SharedWordbookBundleItemTable, JoinType.LEFT, SharedWordbookBundleTable.id, SharedWordbookBundleItemTable.bundleId)
            .select(
                SharedWordbookBundleTable.id,
                SharedWordbookBundleTable.title,
                SharedWordbookBundleTable.slug,
                SharedWordbookBundleTable.description,
                SharedWordbookBundleTable.sharedState,
                SharedWordbookBundleTable.trustedLevel,
                SharedWordbookBundleTable.createdAt,
                UserTable.id,
                UserTable.nickname,
                wordbookCountAlias,
            )
            .where { SharedWordbookBundleTable.id eq bundleId }
            .groupBy(SharedWordbookBundleTable.id, UserTable.id)
            .firstOrNull()
            ?.let { row ->
                SharedWordbookBundleDetailView(
                    id = row[SharedWordbookBundleTable.id].value,
                    title = row[SharedWordbookBundleTable.title],
                    slug = row[SharedWordbookBundleTable.slug]!!,
                    description = row[SharedWordbookBundleTable.description],
                    sharedState = row[SharedWordbookBundleTable.sharedState],
                    trustedLevel = row[SharedWordbookBundleTable.trustedLevel],
                    creator = SimpleUserView(
                        id = row[UserTable.id].value.toString(),
                        nickname = row[UserTable.nickname],
                    ),
                    createdAt = row[SharedWordbookBundleTable.createdAt],
                    wordbookCount = row[wordbookCountAlias].toInt(),
                )
            }
    }

    override fun addWordbook(bundleId: Long, sharedWordbookId: Long) {
        val exists = SharedWordbookBundleItemTable
            .selectAll()
            .where {
                (SharedWordbookBundleItemTable.bundleId eq bundleId) and
                    (SharedWordbookBundleItemTable.sharedWordbookId eq sharedWordbookId)
            }
            .count() > 0
        if (!exists) {
            SharedWordbookBundleItemTable.insert {
                it[SharedWordbookBundleItemTable.bundleId] = EntityID(bundleId, SharedWordbookBundleTable)
                it[SharedWordbookBundleItemTable.sharedWordbookId] = EntityID(sharedWordbookId, SharedWordbookTable)
            }
        }
    }

    override fun removeWordbook(bundleId: Long, sharedWordbookId: Long) {
        SharedWordbookBundleItemTable.deleteWhere {
            (SharedWordbookBundleItemTable.bundleId eq bundleId) and
                (SharedWordbookBundleItemTable.sharedWordbookId eq sharedWordbookId)
        }
    }

    override fun paginateWordbooksInBundle(bundleId: Long, pageable: Pageable): Page<SharedWordbookListView> {
        val total = SharedWordbookBundleItemTable
            .innerJoin(SharedWordbookTable, { SharedWordbookBundleItemTable.sharedWordbookId }, { SharedWordbookTable.id })
            .selectAll()
            .where {
                (SharedWordbookBundleItemTable.bundleId eq bundleId)
            }
            .count()

        val maxVersionAlias = SharedWordbookRevisionTable.version.max().alias("latest_version")

        val content = SharedWordbookBundleItemTable
            .innerJoin(SharedWordbookTable, { SharedWordbookBundleItemTable.sharedWordbookId }, { SharedWordbookTable.id })
            .innerJoin(UserTable, { SharedWordbookTable.creatorId }, { UserTable.id })
            .innerJoin(SharedWordbookRevisionTable, { SharedWordbookTable.id }, { SharedWordbookRevisionTable.sharedWordbookId })
            .select(
                SharedWordbookTable.id,
                SharedWordbookTable.title,
                SharedWordbookTable.slug,
                SharedWordbookTable.sharedState,
                SharedWordbookTable.trustedLevel,
                SharedWordbookTable.createdAt,
                UserTable.id,
                UserTable.nickname,
                maxVersionAlias,
            )
            .where {
                (SharedWordbookBundleItemTable.bundleId eq bundleId)
            }
            .groupBy(SharedWordbookTable.id, UserTable.id)
            .orderBy(SharedWordbookTable.createdAt to SortOrder.DESC, SharedWordbookTable.id to SortOrder.DESC)
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .map { row ->
                SharedWordbookListView(
                    id = row[SharedWordbookTable.id].value,
                    title = row[SharedWordbookTable.title],
                    slug = row[SharedWordbookTable.slug]!!,
                    sharedState = row[SharedWordbookTable.sharedState],
                    trustedLevel = row[SharedWordbookTable.trustedLevel],
                    creator = SimpleUserView(
                        id = row[UserTable.id].value.toString(),
                        nickname = row[UserTable.nickname],
                    ),
                    latestVersion = row[maxVersionAlias]!!,
                    createdAt = row[SharedWordbookTable.createdAt],
                )
            }

        return PageImpl(content, pageable, total)
    }

    override fun paginateByCreatorId(
        creatorId: String,
        pageable: Pageable,
    ): Page<SharedWordbookBundleListView> {
        val uuid = UUID.fromString(creatorId)

        val total = SharedWordbookBundleTable
            .selectAll()
            .where { SharedWordbookBundleTable.creatorId eq uuid }
            .count()

        val wordbookCountAlias = SharedWordbookBundleItemTable.sharedWordbookId.count().alias("wordbook_count")

        val content = SharedWordbookBundleTable
            .join(UserTable, JoinType.INNER, SharedWordbookBundleTable.creatorId, UserTable.id)
            .join(SharedWordbookBundleItemTable, JoinType.LEFT, SharedWordbookBundleTable.id, SharedWordbookBundleItemTable.bundleId)
            .select(
                SharedWordbookBundleTable.id,
                SharedWordbookBundleTable.title,
                SharedWordbookBundleTable.slug,
                SharedWordbookBundleTable.sharedState,
                SharedWordbookBundleTable.trustedLevel,
                SharedWordbookBundleTable.createdAt,
                UserTable.id,
                UserTable.nickname,
                wordbookCountAlias,
            )
            .where { SharedWordbookBundleTable.creatorId eq uuid }
            .groupBy(SharedWordbookBundleTable.id, UserTable.id)
            .orderBy(SharedWordbookBundleTable.createdAt to SortOrder.DESC, SharedWordbookBundleTable.id to SortOrder.DESC)
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .map { row ->
                SharedWordbookBundleListView(
                    id = row[SharedWordbookBundleTable.id].value,
                    title = row[SharedWordbookBundleTable.title],
                    slug = row[SharedWordbookBundleTable.slug]!!,
                    sharedState = row[SharedWordbookBundleTable.sharedState],
                    trustedLevel = row[SharedWordbookBundleTable.trustedLevel],
                    creator = SimpleUserView(
                        id = row[UserTable.id].value.toString(),
                        nickname = row[UserTable.nickname],
                    ),
                    wordbookCount = row[wordbookCountAlias].toInt(),
                    createdAt = row[SharedWordbookBundleTable.createdAt],
                )
            }

        return PageImpl(content, pageable, total)
    }
}
