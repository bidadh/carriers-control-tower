package com.hookiesolutions.cat.subscription.web

import com.hookiesolutions.cat.subscription.common.request.CreateAssetRequest
import com.hookiesolutions.cat.subscription.common.response.AssetResponse
import com.hookiesolutions.cat.subscription.config.OpenAPIConfig
import com.hookiesolutions.cat.subscription.domain.Asset
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
 * @since 22/6/20 12:33
 */
@RestController
@SecurityRequirement(name = OpenAPIConfig.BASIC_SCHEME)
@RequestMapping(AssetController.REQUEST_MAPPING)
class AssetController(
    private val assetService: AssetService,
    private val log: Logger
) {
  @GetMapping("/{enterpriseId}")
  fun findAll(@PathVariable enterpriseId: String): Flux<Asset> {
    log.info("fetching all assets...")
    return assetService.findAll(enterpriseId)
  }

  /**
   * This method saves an asset in mongodb which causes change event and therefore SSE to the clients
   */
  @PostMapping("/{enterpriseId}",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.APPLICATION_JSON_VALUE]
  )
  fun create(
      @PathVariable enterpriseId: String,
      @Valid @RequestBody createAssetRequest: CreateAssetRequest
  ): Mono<Asset> {
    log.info("creating a new asset")
    return assetService.create(createAssetRequest, enterpriseId)
  }

  /**
   * This method publishes an asset to kafka topic which is connected to mongodb and then causes change event and therefore SSE to the clients
   */
  @PostMapping("/create",
      consumes = [MediaType.APPLICATION_JSON_VALUE],
      produces = [MediaType.TEXT_PLAIN_VALUE]
  )
  @Deprecated("deprecated due to remove kafka task")
  fun createAsset(@Valid @RequestBody asset: Asset): Mono<String> {
    return assetService.publish(asset)
  }

  @GetMapping("/byId/{id}")
  fun getById(@PathVariable id: String) = assetService.findById(id)

  @GetMapping("/{enterpriseId}/moving", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun movingAssets(@PathVariable enterpriseId: String): Flux<AssetResponse> {
    log.info("Fetching moving assets for enterprise: '{}'", enterpriseId)
    return assetService.movingAssets(enterpriseId)
        .map{ AssetResponse.from(it) }
  }

  companion object {
    const val REQUEST_MAPPING = "/asset"
  }
}
