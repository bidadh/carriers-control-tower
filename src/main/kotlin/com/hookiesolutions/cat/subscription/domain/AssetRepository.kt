package com.hookiesolutions.cat.subscription.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 22/6/20 12:16
 */
interface AssetRepository: ReactiveMongoRepository<Asset, String> {
  fun findByDeviceId(deviceId: String): Mono<Asset>
}
