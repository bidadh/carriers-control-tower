package com.hookiesolutions.cat.subscription.common.request

import javax.validation.constraints.NotBlank

/**
 *
 * @author Arthur Kazemi<bidadh@gmail.com>
 * @since 26/6/20 10:17
 */
data class CreateAssetRequest(
    @NotBlank
    val name: String,
    @NotBlank
    val deviceId: String,
    @NotBlank
    val assetTypeId: String
)
