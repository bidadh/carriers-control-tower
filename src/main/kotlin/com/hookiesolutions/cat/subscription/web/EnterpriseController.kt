package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.config.OpenAPIConfig
import com.hookiesolutions.cat.subscription.domain.Enterprise
import com.hookiesolutions.cat.subscription.domain.EnterpriseRepository
import com.hookiesolutions.cat.subscription.domain.Geofence
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import javax.validation.Valid

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 24/6/20 17:59
 */
@RestController
@SecurityRequirement(name = OpenAPIConfig.BASIC_SCHEME)
@RequestMapping(EnterpriseController.REQUEST_MAPPING)
class EnterpriseController(
    private val enterpriseRepository: EnterpriseRepository,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val log: Logger

) {
  @PostMapping("",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE]
  )
  fun createEnterprise(@Valid @RequestBody en: Enterprise): Mono<Enterprise> {
    log.info("Saving an Enterprise: '{}'", en.name)
    return enterpriseRepository.save(en)
  }

  @PostMapping( "/{enterpriseId}/locations",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE]
  )
  fun addLocation(@PathVariable enterpriseId: String, @Valid @RequestBody geofence: Geofence): Mono<Enterprise> {
    log.info("Adding a geofence to an existing Enterprise")
    return mongoTemplate
        .findAndModify(
            query(where("id").`is`(enterpriseId)),
            Update().addToSet("locations", geofence),
            FindAndModifyOptions.options().returnNew(true),
            Enterprise::class.java
        )
        .doOnNext { log.info("Enterprise has been saved with its new geofence") }
  }

  @GetMapping("/locations", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun allLocations(): Flux<Geofence> {
    return enterpriseRepository.findAll()
        .map { en -> en.locations }
        .flatMap { it.toFlux() }
  }

  @GetMapping("/myEnterprise", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun myEnterprise(): Mono<Enterprise> {
    return enterpriseRepository.findAll().last()
  }

  @GetMapping("/{enterpriseId}", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun fetchById(@PathVariable enterpriseId: String): Mono<Enterprise> {
    return enterpriseRepository.findById(enterpriseId)
  }

  @GetMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun fetchAll(): Flux<Enterprise> {
    return enterpriseRepository.findAll()
  }

  companion object {
    const val REQUEST_MAPPING = "/enterprise"
  }
}
