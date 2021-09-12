package com.hookiesolutions.cat.subscription.domain

import com.hookiesolutions.cat.subscription.common.model.AbstractEntity
import com.hookiesolutions.cat.subscription.config.GeoJsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@Document(collection = "location")
@TypeAlias("location")
data class DevicePositionUpdate(
    @Indexed
    val deviceId: String,
    @Indexed
    val enterpriseId: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Indexed
    val time: LocalDateTime,
    val sentBy: String,
    @JsonDeserialize(using = GeoJsonDeserializer::class)
    val position: GeoJsonPoint,
    val properties: Map<String, Any> = emptyMap()
) : AbstractEntity() {
  fun toPositionUpdate(): PositionUpdate {
    return PositionUpdate(this.position, this.time, this.properties)
  }

  companion object {
    class Queries {
      companion object {
        fun enterpriseId(enterpriseId: String): Criteria {
          return where("enterpriseId")
              .`is`(enterpriseId)
        }

        fun enterpriseId(asset: Asset): Criteria {
          return enterpriseId(asset.assetType.enterpriseId)
        }
      }
    }
  }
}
