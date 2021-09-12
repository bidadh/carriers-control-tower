package com.hookiesolutions.cat.subscription.ruleengine.rule

import com.hookiesolutions.cat.subscription.common.model.LocationsDefinition
import com.hookiesolutions.cat.subscription.domain.Asset
import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import com.hookiesolutions.cat.subscription.domain.Event
import com.hookiesolutions.cat.subscription.ruleengine.EventRule
import com.hookiesolutions.cat.subscription.service.AssetService
import com.hookiesolutions.cat.subscription.service.TimeMachine
import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.integrationFlow
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 25/8/20 00:43
 */
@Component
class EnterExitEventRule(
    private val enteredLocationChannel: MessageChannel,
    private val exitedLocationChannel: MessageChannel,
    private val saveEventChannel: MessageChannel,
    private val locationEventSubscriptionsChannel: MessageChannel,
    private val locationDefinitionDispatcherChannel: MessageChannel,
    private val movingAssetsLocationHandlerChannel: MessageChannel,
    private val enteredLocationsOutputChannel: MessageChannel,
    private val exitedLocationsOutputChannel: MessageChannel,
    private val timeMachine: TimeMachine,
    private val assetService: AssetService,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val log: Logger
): EventRule {
  override val channel: String = "EnterExitEventRuleChannel"

  @ServiceActivator(inputChannel = "EnterExitEventRuleChannel")
  override fun process(payload: DevicePositionUpdate) {
    val q = Query.query(Criteria.where("deviceId").`is`(payload.deviceId))
    val currentPosition = payload.toPositionUpdate()

    val u = Update()
        .set("currentPosition", currentPosition)
        .set("auditAction", "PositionUpdate")

    u.inc("numberOfPositionUpdates")

    log.info("Position update received for: '{}', '{}'", payload.deviceId, payload.time)
    mongoTemplate.find(q, Asset::class.java)
        .doOnNext {
          u.set("lastPosition", it.currentPosition)
          log.info("Asset found with deviceId: '{}'", it.deviceId)
        }
        .flatMap { assetService.locationsFor(currentPosition, it.assetType.enterpriseId) }
        .flatMap { list ->
          val currentLocations = list.map { it.name }
          u.set("currentLocations", currentLocations)
          log.info("Setting Asset's currentLocations to: '{}'", currentLocations)
          mongoTemplate.updateFirst(q, u, Asset::class.java)
        }
        .subscribe {
          log.info("Issued an update to Asset (device: '{}') withIssued an update to Asset (device: '{}') with result: '{}' result: '{}'", payload.deviceId, it)
        }
  }

  @Bean
  fun movingAssetsLocationHandlerFlow(): IntegrationFlow {
    return integrationFlow {
      channel(movingAssetsLocationHandlerChannel)
      transform<Asset> { assetService.locationsDefinitionFor(it) }
      handle { p: Mono<LocationsDefinition>, _: MessageHeaders ->
        p.subscribe {
          locationDefinitionDispatcherChannel.send(MessageBuilder.withPayload(it).build())
        }
      }
    }
  }

  @Bean
  fun assetLocationNotifier(logChannel: MessageChannel): IntegrationFlow {
    return integrationFlow {
      channel(locationDefinitionDispatcherChannel)
      routeToRecipients {
        applySequence(true)
        ignoreSendFailures(true)
        recipient<LocationsDefinition>(exitedLocationChannel) { it.hasExited }
        recipient<LocationsDefinition>(enteredLocationChannel) { it.hasEntered }
        defaultOutputChannel(logChannel)
      }
    }
  }

  @Bean
  fun enteredLocationFlow(): IntegrationFlow {
    return IntegrationFlows
        .from(enteredLocationChannel)
        .transform<LocationsDefinition, Event> { Event.entered(it, timeMachine.now()) }
        .routeToRecipients { router ->
          router
              .applySequence(true)
              .ignoreSendFailures(true)
              .recipient(saveEventChannel)
              .recipient(enteredLocationsOutputChannel)
              .recipient(locationEventSubscriptionsChannel)
        }
        .get()
  }

  @Bean
  fun exitedLocationFlow(): IntegrationFlow {
    return IntegrationFlows
        .from(exitedLocationChannel)
        .transform<LocationsDefinition, Event> { Event.exited(it, timeMachine.now()) }
        .routeToRecipients { router ->
          router
              .applySequence(true)
              .ignoreSendFailures(true)
              .recipient(saveEventChannel)
              .recipient(exitedLocationsOutputChannel)
              .recipient(locationEventSubscriptionsChannel)
        }
        .get()
  }

  override fun accept(source: DevicePositionUpdate): Boolean {
    return true
  }
}
