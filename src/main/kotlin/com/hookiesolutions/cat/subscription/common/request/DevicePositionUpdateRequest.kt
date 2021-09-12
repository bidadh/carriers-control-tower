package com.hookiesolutions.cat.subscription.common.request

import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import javax.validation.constraints.NotNull

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/6/20 02:30
 */
data class DevicePositionUpdateRequest(
    val enterpriseId: String,
    val deviceId: String,
    @NotNull
    val position: GeoJsonPoint,
    val properties: Map<String, Any> = emptyMap()
)
