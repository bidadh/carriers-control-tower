package com.hookiesolutions.cat.subscription.domain

import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import java.time.LocalDateTime

data class Geofence(
    val name: String,
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val polygon: GeoJsonPolygon,
    val properties: Map<String, Any> = emptyMap(),
    val eventUpdate: EventUpdate? = null,
    val numberOfAssets: Int = 0,
    val isFavourite: Boolean = false,
    val criticality: Criticality = Criticality.Normal
)

data class EventUpdate(
    val updatedAt: LocalDateTime,
    val action: LocationEventAction
)

@Suppress("unused")
enum class Criticality {
  Low,
  Normal,
  High,
  Extreme
}
