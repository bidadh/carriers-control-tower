package com.hookiesolutions.cat.subscription.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 24/6/20 23:06
 */
interface AssetTypeRepository: ReactiveMongoRepository<AssetType, String>
