package com.hookiesolutions.cat.subscription.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 2/7/20 15:55
 */
interface EventRepository: ReactiveMongoRepository<Event, String>
