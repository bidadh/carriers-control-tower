package com.hookiesolutions.cat.subscription.domain

import com.hookiesolutions.cat.subscription.common.model.AbstractEntity
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 24/6/20 00:31
 */
@Document
@TypeAlias("assetType")
data class AssetType(
    @Indexed
    val name: String,
    val description: String,
    @Indexed
    val enterpriseId: String
) : AbstractEntity()
