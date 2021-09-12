package com.hookiesolutions.cat.subscription.common.message

import com.hookiesolutions.cat.subscription.domain.AssetTypeInfo
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class AssetMessage(
    val name: String,
    val deviceId: String,
    val assetType: AssetTypeInfo,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val createdDate: LocalDateTime,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val lastModifiedDate: LocalDateTime? = null,
    val createdBy: String,
    val lastModifiedBy: String,
    val version: Long = 1,
    val auditAction: String = "Create"
)
