package com.hookiesolutions.cat.subscription.domain

import com.hookiesolutions.cat.subscription.common.model.AbstractEntity
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 24/6/20 00:28
 */
@Document
@TypeAlias("enterprise")
data class Enterprise(
    val name: String,
    val locations: List<Geofence>
): AbstractEntity()
