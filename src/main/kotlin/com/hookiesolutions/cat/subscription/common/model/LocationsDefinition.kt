package com.hookiesolutions.cat.subscription.common.model

import com.hookiesolutions.cat.subscription.domain.AssetInfo
import com.hookiesolutions.cat.subscription.domain.Geofence
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotificationData

data class LocationsDefinition(
    val asset: AssetInfo,
    private val previousLocations: List<Geofence>,
    private val currentLocations: List<Geofence>
) : SSENotificationData {
  val entered = currentLocations.minus(previousLocations)
  val exited = previousLocations.minus(currentLocations)

  val hasEntered = entered.isNotEmpty()
  val hasExited = exited.isNotEmpty()
}
