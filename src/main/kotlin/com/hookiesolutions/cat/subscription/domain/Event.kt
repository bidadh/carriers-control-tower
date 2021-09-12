package com.hookiesolutions.cat.subscription.domain

import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData
import com.hookiesolutions.cat.subscription.common.model.AbstractEntity
import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import com.hookiesolutions.cat.subscription.common.model.LocationsDefinition
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import java.time.LocalDateTime

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 2/7/20 15:54
 */
@Document
@TypeAlias("event")
data class Event(
    @Indexed
    val asset: AssetInfo,
    val locations: List<Geofence>,
    val action: LocationEventAction,
    val time: LocalDateTime,
    val criticality: Criticality = Criticality.Normal
): AbstractEntity(), SSENotificationData {
  companion object {
    fun entered(ld: LocationsDefinition, at: LocalDateTime): Event {
      val criticality = ld.entered.firstOrNull()?.criticality ?: Criticality.Normal
      return Event(ld.asset, ld.entered, LocationEventAction.ENTERED, at, criticality)
    }

    fun exited(ld: LocationsDefinition, at: LocalDateTime): Event {
      val criticality = ld.exited.firstOrNull()?.criticality ?: Criticality.Normal
      return Event(ld.asset, ld.exited, LocationEventAction.EXITED, at, criticality)
    }

    class Queries {
      companion object {
        fun enterpriseId(enterpriseId: String): Criteria {
          return where("asset.assetType.enterpriseId")
              .`is`(enterpriseId)
        }

        fun enterpriseId(event: Event): Criteria {
          return enterpriseId(event.asset.assetType.enterpriseId)
        }
      }
    }
  }
}
