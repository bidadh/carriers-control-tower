package com.hookiesolutions.cat.subscription.common.model

@Deprecated("we don't use LocationAssetCount event anymore, we use enterprise update change stream instead")
data class LocationAssetCount(
    val name: String,
    val count: Long
)
