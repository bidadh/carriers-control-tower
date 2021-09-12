package com.hookiesolutions.cat.subscription.config.stream

import com.hookiesolutions.cat.subscription.common.event.LocationSSENotification
import com.hookiesolutions.cat.subscription.common.event.sse.GenericSSENotification
import com.hookiesolutions.cat.subscription.common.event.sse.GenericSSENotificationData
import com.hookiesolutions.cat.subscription.common.event.sse.SSENotification
import com.hookiesolutions.cat.subscription.common.model.LocationEventAction
import com.hookiesolutions.cat.subscription.config.stream.Bindings.Companion.Channels.Companion.Input.Companion.DEVICE_POSITIONS_INPUT_CHANNEL
import com.hookiesolutions.cat.subscription.domain.*
import com.hookiesolutions.cat.subscription.service.EventService
import com.mongodb.client.model.changestream.FullDocument
import com.mongodb.client.model.changestream.OperationType
import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ChangeStreamOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.MediaType
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.mongodb.dsl.MongoDb
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.reactive.function.client.WebClient


/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 20:30
 */
@Configuration
class FlowsConfig(
    private val movingAssetsChannel: MessageChannel,
    private val positionUpdateCountChannel: MessageChannel,
    private val eventCountChannel: MessageChannel,
    private val assetWithEventsCountChannel: MessageChannel,
    private val locationLastActiveTimeChannel: MessageChannel,
    private val movingAssetsLocationHandlerChannel: MessageChannel,
    private val locationsSseChannel: MessageChannel,
    private val sseChannel: SubscribableChannel,
    private val saveEventChannel: MessageChannel,
    private val locationEventSubscriptionsChannel: MessageChannel,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val eventService: EventService,
    private val log: Logger
) {
  @Bean
  fun startedMovingAssetsFlow(): IntegrationFlow {
    val match = match(
        where("operationType").`is`(OperationType.UPDATE.value)
            .and("fullDocument.numberOfPositionUpdates").`is`(1)
    )
    val agg = newAggregation(match)
    val changeStreamOptions = ChangeStreamOptions.builder()
        .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
        .filter(agg)
        .build()
    val producerSpec = MongoDb
        .changeStreamInboundChannelAdapter(mongoTemplate)
        .domainType(Asset::class.java)
        .collection("asset")
        .options(changeStreamOptions)
        .extractBody(false)
    return IntegrationFlows.from(producerSpec)
        .transform<Message<ChangeStreamEvent<Asset>>, SSENotification> {
          val notification = SSENotification.assetStartedMoving(asset = it.payload.body!!)
          log.debug("Sending SSE for: '{}', '{}'", notification.type, notification.data)

          notification
        }
        .channel(sseChannel)
        .get()!!
  }

  @Bean
  fun positionUpdateFlow(): IntegrationFlow {
    val match = match(
        where("operationType").`is`(OperationType.UPDATE.value)
            .and("updateDescription.updatedFields.lastPosition").exists(true)
    )
    val changeStreamOptions = ChangeStreamOptions.builder()
        .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
        .filter(newAggregation(match))
        .build()
    val producerSpec = MongoDb
        .changeStreamInboundChannelAdapter(mongoTemplate)
        .domainType(Asset::class.java)
        .collection("asset")
        .options(changeStreamOptions)
        .extractBody(true)
    return IntegrationFlows.from(producerSpec)
//        .transform<Message<Asset>, Asset> {
//          log.info("Received position update on asset. device '{}'", it.payload.deviceId)
//          it.payload
//        }
        .routeToRecipients { router ->
          router
              .applySequence(true)
              .ignoreSendFailures(true)
              .recipient(movingAssetsChannel)
              .recipient(positionUpdateCountChannel)
              .recipient(movingAssetsLocationHandlerChannel)
        }
        .get()!!
  }

  @Bean
  fun newEventsFlow(): IntegrationFlow {
    val match = match(where("operationType").`is`(OperationType.INSERT.value))
    val changeStreamOptions = ChangeStreamOptions.builder()
        .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
        .filter(newAggregation(match))
        .build()
    val producerSpec = MongoDb
        .changeStreamInboundChannelAdapter(mongoTemplate)
        .domainType(Event::class.java)
        .collection("event")
        .options(changeStreamOptions)
        .extractBody(true)

    return IntegrationFlows.from(producerSpec)
//        .transform<Message<Event>, Event> { it.payload }
        .routeToRecipients { router ->
          router
              .applySequence(true)
              .ignoreSendFailures(true)
              .recipient(locationsSseChannel)
              .recipient(eventCountChannel)
              .recipient(assetWithEventsCountChannel)
              .recipient(locationLastActiveTimeChannel)
        }
        .get()!!
  }

  @Bean
  fun newDevicePositionUpdateFlow(): IntegrationFlow {
    val match = match(where("operationType").`is`(OperationType.INSERT.value))
    val changeStreamOptions = ChangeStreamOptions.builder()
        .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
        .filter(newAggregation(match))
        .build()
    val producerSpec = MongoDb
        .changeStreamInboundChannelAdapter(mongoTemplate)
        .domainType(DevicePositionUpdate::class.java)
        .collection("location")
        .options(changeStreamOptions)
        .extractBody(true)                    

    return IntegrationFlows.from(producerSpec)
//        .transform<Message<DevicePositionUpdate>, DevicePositionUpdate> { it.payload }
        .channel(DEVICE_POSITIONS_INPUT_CHANNEL)
        .get()!!
  }

  @Bean
  fun locationLastActiveTimeFlow(): IntegrationFlow {
    return integrationFlow {
      channel(locationLastActiveTimeChannel)
      handle { e: Event, _: MessageHeaders ->
        val locationName = e.locations.first().name

        val q = query(where("locations").elemMatch(where("name").`is`(locationName))
            .andOperator(where("id").`is`(e.asset.assetType.enterpriseId)))

        val inc = if (e.action == LocationEventAction.ENTERED) 1 else -1
        val eventUpdate = EventUpdate(e.time, e.action)
        val update = Update()
            .set("locations.$.eventUpdate", eventUpdate)
            .inc("locations.$.numberOfAssets", inc)

        mongoTemplate.updateFirst(q, update, Enterprise::class.java)
            .subscribe {
              log.info("Enterprise is updated with the event")
            }
      }
    }
  }

  @Bean
  fun enterpriseLocationsUpdateFlow(): IntegrationFlow {
    val match = match(
        where("operationType").`is`(OperationType.UPDATE.value)
//            .and("updateDescription.updatedFields.locations").exists(true)
    )
    val changeStreamOptions = ChangeStreamOptions.builder()
        .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
        .filter(newAggregation(match))
        .build()
    val producerSpec = MongoDb
        .changeStreamInboundChannelAdapter(mongoTemplate)
        .domainType(Enterprise::class.java)
        .collection("enterprise")
        .options(changeStreamOptions)
        .extractBody(true)
    return IntegrationFlows.from(producerSpec)
        .transform<Enterprise, SSENotification> { SSENotification.enterpriseLocationEvents(it) }
        .channel(sseChannel)
        .get()!!
  }

  @Bean
  fun insertAssetCountFlow(): IntegrationFlow {
    val match = match(where("operationType").`is`(OperationType.INSERT.value))
    val changeStreamOptions = ChangeStreamOptions.builder()
        .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
        .filter(newAggregation(match))
        .build()
    val producerSpec = MongoDb
        .changeStreamInboundChannelAdapter(mongoTemplate)
        .domainType(Asset::class.java)
        .collection("asset")
        .options(changeStreamOptions)
        .extractBody(true)
    return IntegrationFlows.from(producerSpec)
        .handle { asset: Asset, _: MessageHeaders ->
          mongoTemplate.count(query(Asset.Companion.Queries.enterpriseId(asset)), Asset::class.java)
              .subscribe {
                val genericSSENotification = GenericSSENotification("AssetCount", GenericSSENotificationData(it))
                sseChannel.send(MessageBuilder.withPayload(genericSSENotification).build())
              }
        }
        .get()!!
  }

  @Bean
  fun movingAssetsFlow(): IntegrationFlow {
    return integrationFlow {
      channel(movingAssetsChannel)
      transform<Asset> { SSENotification.assetMoved(asset = it) }
      channel(sseChannel)
    }
  }

  @Bean
  fun positionUpdateCountFlow(): IntegrationFlow {
    return integrationFlow {
      channel(positionUpdateCountChannel)
      handle { p: Asset, _: MessageHeaders ->
        mongoTemplate.count(query(DevicePositionUpdate.Companion.Queries.enterpriseId(p)), DevicePositionUpdate::class.java)
            .subscribe {
              val genericSSENotification = GenericSSENotification("PositionUpdateCount", GenericSSENotificationData(it))
              sseChannel.send(MessageBuilder.withPayload(genericSSENotification).build())
            }
      }
    }
  }

  @Bean
  fun eventCountFlow(): IntegrationFlow {
    return integrationFlow {
      channel(eventCountChannel)
      handle { p: Event, _: MessageHeaders ->
        mongoTemplate.count(query(Event.Companion.Queries.enterpriseId(p)), Event::class.java)
            .subscribe {
              val genericSSENotification = GenericSSENotification("EventCount", GenericSSENotificationData(it))
              sseChannel.send(MessageBuilder.withPayload(genericSSENotification).build())
            }
      }
    }
  }

  @Bean
  fun assetWithEventsCountFlow(): IntegrationFlow {
    return integrationFlow {
      channel(assetWithEventsCountChannel)
      handle { p: Event, _: MessageHeaders ->
        eventService.uniqueNumberOfAssetsWithEvents(p)
            .subscribe {
              val data = SSENotification.uniqueNumberOfAssetsWithEvents(it)
              sseChannel.send(MessageBuilder.withPayload(data).build())
            }
      }
    }
  }

  @Bean
  fun saveEventsFlow(): IntegrationFlow {
    return integrationFlow {
      channel(saveEventChannel)
      handle { p: Event, _: MessageHeaders ->
        eventService.saveEvent(p)
      }
    }
  }

  @Bean
  fun postToSubscribersFlow(postToSubscribersChannel: MessageChannel): IntegrationFlow {
    return integrationFlow {
      channel(locationEventSubscriptionsChannel)
      transform(Transformers.toJson(MediaType.APPLICATION_JSON_VALUE))
      channel(postToSubscribersChannel)
    }
  }

  @Bean
  fun locationsSseFlow(): IntegrationFlow {
    return integrationFlow {
      channel(locationsSseChannel)
      transform<Event> {
        LocationSSENotification(it)
      }
      channel(sseChannel)
    }
  }

/*
  @ServiceActivator(inputChannel = "postToSubscribersChannel")
  @Bean
  fun reactiveOutbound(client: WebClient, logChannel: MessageChannel): WebFluxRequestExecutingMessageHandler {
    val handler = WebFluxRequestExecutingMessageHandler("http://localhost:8080/subscription/handleEvent", client)
    handler.setPublisherElementType(Event::class.java)
    handler.setHttpMethod(HttpMethod.POST)
    handler.setExpectedResponseType(String::class.java)
    handler.outputChannel = logChannel
    return handler
  }
*/

  @Bean
  fun logFlow(logChannel: MessageChannel): IntegrationFlow {
    return integrationFlow {
      channel(logChannel)
      handle {
        log.info("'{}'", it.payload)
      }
    }
  }

  @Bean
  fun webClientBuilder(): WebClient.Builder {
    return WebClient.builder()
  }

  @Bean
  fun webClient(b: WebClient.Builder): WebClient {
    return b.build()
  }
}
