package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.config.OpenAPIConfig
import com.hookiesolutions.cat.subscription.domain.AssetType
import com.hookiesolutions.cat.subscription.service.AssetService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 24/6/20 23:42
 */
@RestController
@SecurityRequirement(name = OpenAPIConfig.BASIC_SCHEME)
@RequestMapping(AssetTypeController.REQUEST_MAPPING)
class AssetTypeController(
    private val assetService: AssetService,
    private val log: Logger
) {
  @GetMapping("/{enterpriseId}")
  fun findAll(@PathVariable enterpriseId: String): Flux<AssetType> {
    log.info("fetching all asset types...")
    return assetService.findAllTypesFor(enterpriseId)
  }

  @PostMapping("",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE]
  )
  fun create(@Valid @RequestBody assetType: AssetType): Mono<AssetType> {
    log.info("creating a new asset type")
    return assetService.saveType(assetType)
  }

  @GetMapping("/{id}")
  fun getById(@PathVariable id: String) = assetService.findTypeById(id)

  companion object {
    const val REQUEST_MAPPING = "/assetType"
  }
}
