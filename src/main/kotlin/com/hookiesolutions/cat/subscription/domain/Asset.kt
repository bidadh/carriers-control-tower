package com.hookiesolutions.cat.subscription.domain

import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData
import com.hookiesolutions.cat.subscription.common.model.AbstractEntity
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 12:15
 */
@Document
@TypeAlias("asset")
data class Asset(
    @NotBlank
    @Indexed
    val name: String,

    @Indexed(unique = true)
    @NotBlank
    val deviceId: String,

    @NotBlank
    @Indexed
    val assetType: AssetTypeInfo,

    val lastPosition: PositionUpdate? = null,
    val currentPosition: PositionUpdate? = null,
    val numberOfPositionUpdates: Int = 0,
    val positions: List<PositionUpdate> = emptyList(),
    val currentLocations: List<String> = emptyList()
) : AbstractEntity(), SSENotificationData {
  fun assetInfo(): AssetInfo {
    return AssetInfo(
        this.name,
        this.deviceId,
        this.assetType,
        this.currentPosition
    )
  }

  companion object {
    class Queries {
      companion object {
        fun enterpriseId(enterpriseId: String): Criteria {
          return where("assetType.enterpriseId")
              .`is`(enterpriseId)
        }

        fun enterpriseId(asset: Asset): Criteria {
          return enterpriseId(asset.assetType.enterpriseId)
        }
      }
    }
  }
}

data class PositionUpdate(
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val position: GeoJsonPoint,
    val updatedAt: LocalDateTime,
    val properties: Map<String, Any> = emptyMap()
)

data class AssetTypeInfo(
    val name: String,
    @Indexed
    val enterpriseId: String
) {
  companion object {
    fun from(assetType: AssetType): AssetTypeInfo {
      return AssetTypeInfo(assetType.name, assetType.enterpriseId)
    }
  }
}

data class AssetInfo(
    val name: String,
    val deviceId: String,
    val assetType: AssetTypeInfo,
    val currentPosition: PositionUpdate?
)

