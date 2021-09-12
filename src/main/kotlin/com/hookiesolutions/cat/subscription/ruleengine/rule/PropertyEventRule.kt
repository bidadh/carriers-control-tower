package com.hookiesolutions.cat.subscription.ruleengine.rule

import com.hookiesolutions.cat.subscription.domain.AssetRepository
import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import com.hookiesolutions.cat.subscription.ruleengine.EventRule
import com.hookiesolutions.cat.subscription.ruleengine.rule.property.PropertyEventRule
import org.slf4j.Logger
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/8/20 14:37
 */
@Component
class PropertyEventRule(
    private val rules: List<PropertyEventRule>,
    private val assetRepository: AssetRepository,
    private val log: Logger
) : EventRule {
  override val channel: String = PROPERTY_EVENT_RULE_ENGINE_CHANNEL

  @ServiceActivator(inputChannel = PROPERTY_EVENT_RULE_ENGINE_CHANNEL)
  override fun process(payload: DevicePositionUpdate) {
    log.info("Processing position update for: '{}'", payload.deviceId)
    assetRepository.findByDeviceId(payload.deviceId)
        .subscribe { asset ->
          rules
              .filter { it.accept(payload) }
              .forEach { it.process(asset, payload) }
        }
  }

  override fun accept(source: DevicePositionUpdate): Boolean {
    val availableKeys = rules.map { it.key }
    return availableKeys
        .stream()
        .anyMatch {
          source.properties.containsKey(it)
        }
  }

  companion object {
    const val PROPERTY_EVENT_RULE_ENGINE_CHANNEL = "PropertyEventRuleEngineChannel"
  }
}
