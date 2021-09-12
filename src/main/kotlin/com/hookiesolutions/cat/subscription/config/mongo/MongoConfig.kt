package com.hookiesolutions.cat.subscription.config.mongo

import com.hookiesolutions.cat.subscription.domain.Asset
import com.hookiesolutions.cat.subscription.domain.AssetType
import com.hookiesolutions.cat.subscription.domain.DevicePositionUpdate
import com.hookiesolutions.cat.subscription.domain.Enterprise
import com.hookiesolutions.cat.subscription.domain.Event
import org.slf4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.config.GeoJsonConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 13:52
 */
@Configuration
class MongoConfig(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val mongoMappingContext: MongoMappingContext,
    private val logger: Logger
): GeoJsonConfiguration() {
  @EventListener(ApplicationReadyEvent::class)
  fun initIndicesAfterStartup() {
    val resolver = MongoPersistentEntityIndexResolver(mongoMappingContext)

    Flux.just(Asset::class.java, AssetType::class.java, Enterprise::class.java, DevicePositionUpdate::class.java, Event::class.java)
        .flatMap { clazz ->
          resolver
              .resolveIndexFor(clazz)
              .toFlux()
              .flatMap { Mono.zip(mongoTemplate.indexOps(clazz).toMono(), it.toMono()) }
        }
        .flatMap {
          val indexOps = it.t1
          val def = it.t2
          indexOps.ensureIndex(def)
        }
        .subscribe { name -> logger.info("ensureIndex: '{}'", name) }
  }
}
