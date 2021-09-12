package com.hookiesolutions.cat.subscription.service

import com.hookiesolutions.cat.subscription.common.exception.DocumentNotFoundException
import com.hookiesolutions.cat.subscription.common.message.AssetMessage
import com.hookiesolutions.cat.subscription.common.model.LocationsDefinition
import com.hookiesolutions.cat.subscription.common.request.CreateAssetRequest
import com.hookiesolutions.cat.subscription.domain.*
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.slf4j.Logger
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 12:31
 */
@Service
class AssetService(
    private val assetRepository: AssetRepository,
    private val assetTypeRepository: AssetTypeRepository,
    private val enterpriseRepository: EnterpriseRepository,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val assetsOutputChannel: MessageChannel,
    private val timeMachine: TimeMachine,
    private val securityHandler: SecurityHandler,
    private val log: Logger
) {
  fun findAll(enterpriseId: String): Flux<Asset> {
    log.info("fetching all assets")
    val criteria = Asset.Companion.Queries.enterpriseId(enterpriseId)
    return mongoTemplate.find(query(criteria), Asset::class.java)
  }

  fun create(createAssetRequest: CreateAssetRequest, enterpriseId: String): Mono<Asset> {
    log.info("creating aa asset")
    return assetTypeRepository.findById(createAssetRequest.assetTypeId)
        .switchIfEmpty(DocumentNotFoundException("Asset Type with the given id (${createAssetRequest.assetTypeId}) does not exist").toMono())
        .filter { it.enterpriseId == enterpriseId }
        .switchIfEmpty(AccessDeniedException("asset type doesn't belong to enterprise!").toMono())
        .map { AssetTypeInfo.from(it) }
        .map { Asset(createAssetRequest.name, createAssetRequest.deviceId, it) }
        .flatMap { assetRepository.save(it) }
  }

  fun findById(@Parameter(`in` = ParameterIn.PATH) assetIdd: String): Mono<Asset> {
    log.info("fetching aa asset by: '{}'", assetIdd)
    return assetRepository.findById(assetIdd)
  }

  fun locationsDefinitionFor(asset: Asset): Mono<LocationsDefinition> {
    if (asset.currentPosition == null) {
      return Mono.empty()
    }

    val assetInfo = asset.assetInfo()

    if (asset.lastPosition == null) {
      return locationsFor(asset.currentPosition, asset.assetType.enterpriseId)
          .map {
            LocationsDefinition(
                asset = assetInfo,
                previousLocations = emptyList(),
                currentLocations = it
            )
          }
    }

    val previousPosition = asset.lastPosition

    return locationsFor(previousPosition, asset.assetType.enterpriseId)
        .zipWith(locationsFor(asset.currentPosition, asset.assetType.enterpriseId))
        .map {
          LocationsDefinition(
              asset = assetInfo,
              previousLocations = it.t1,
              currentLocations = it.t2
          )
        }
  }

  fun locationsFor(position: PositionUpdate, enterpriseId: String): Mono<List<Geofence>> {
    log.info("Fetching All locations for the position: '{}'", position.position.coordinates)
    return enterpriseRepository.locationsFor(position.position, enterpriseId)
        .collectList()
        .doOnNext {
          log.info("'{}' Locations found for position", it.size)
        }
  }

  fun publish(asset: Asset): Mono<String> {
    return securityHandler.loggedInUsername()
        .map {
          return@map AssetMessage(
              asset.name,
              asset.deviceId,
              asset.assetType,
              timeMachine.now(),
              null,
              it,
              it
          )
        }
        .doOnNext {
          log.debug("Publishing asset creation message to Kafka....'{}'", it)
          assetsOutputChannel
              .send(MessageBuilder.withPayload(it).build())
        }
        .map { asset.name }
  }

  fun movingAssets(enterpriseId: String): Flux<Asset> {
    val qry = query(
        Asset.Companion.Queries.enterpriseId(enterpriseId)
            .and("numberOfPositionUpdates").gt(0)
    )

    return mongoTemplate.find(qry, Asset::class.java)
  }

  fun findAllTypesFor(enterpriseId: String): Flux<AssetType> {
    val qry = query(where("enterpriseId").`is`(enterpriseId))

    return mongoTemplate.find(qry, AssetType::class.java)
  }

  fun saveType(assetType: AssetType): Mono<AssetType> {
    return assetTypeRepository.save(assetType)
  }

  fun findTypeById(id: String): Mono<AssetType> {
    return assetTypeRepository.findById(id)
  }
}
