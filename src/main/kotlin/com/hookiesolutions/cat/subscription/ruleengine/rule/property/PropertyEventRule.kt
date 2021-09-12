package com.hookiesolutions.cat.subscription.ruleengine.rule.property

import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import com.hookiesolutions.cat.subscription.domain.Asset
import com.hookiesolutions.cat.subscription.domain.AssetInfo
import com.hookiesolutions.cat.subscription.domain.Criticality
import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import com.hookiesolutions.cat.subscription.domain.Event
import com.hookiesolutions.cat.subscription.domain.PositionUpdate
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/8/20 14:02
 */
abstract class PropertyEventRule(
    private var saveEventChannel: MessageChannel
) {
  abstract val key: String
  abstract val action: LocationEventAction
  lateinit var assetInfo: AssetInfo
  lateinit var event: Event
  private lateinit var positionUpdate: DevicePositionUpdate

  abstract fun processEvent()

  val value: Any
    get() = positionUpdate.properties.getValue(key)

  private val criticality: Criticality
    get() {
      val c = assetInfo.currentPosition?.properties?.get(CRITICALITY_PROP_KEY) as String?
          ?: Criticality.Normal.name
      return Criticality.valueOf(c)
    }

  fun accept(payload: DevicePositionUpdate): Boolean {
    return payload.properties.containsKey(key)
  }

  private fun generateEvent() {
    this.event = Event(
        this.assetInfo,
        emptyList(),
        action,
        assetInfo.currentPosition!!.updatedAt,
        criticality
    )
  }

  fun process(asset: Asset, payload: DevicePositionUpdate) {
    this.positionUpdate = payload

    val cp = PositionUpdate(
        payload.position,
        payload.time,
        payload.properties
            .plus("message" to value)
    )
    val assetInfo = AssetInfo(asset.name, asset.deviceId, asset.assetType, cp)

    this.assetInfo = assetInfo
    generateEvent()
    processEvent()
    saveEventChannel.send(MessageBuilder.withPayload(event).build())
  }

  companion object {
    const val CRITICALITY_PROP_KEY = "criticality"
  }
}
