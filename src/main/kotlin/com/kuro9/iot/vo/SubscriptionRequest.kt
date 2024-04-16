package com.kuro9.iot.vo

import com.smartthings.sdk.client.models.CapabilitySubscriptionDetail
import com.smartthings.sdk.client.models.DeviceSubscriptionDetail
import com.smartthings.sdk.client.models.SubscriptionSource

sealed interface SubscriptionRequest {
    val appId: String
    val sourceType: SubscriptionSource

    data class DeviceSubscriptionRequest(
        override val appId: String,
        val device: DeviceSubscriptionDetail
    ) : SubscriptionRequest {
        override val sourceType = SubscriptionSource.DEVICE
    }

    data class CapabilitySubscriptionRequest(
        override val appId: String,
        val capability: CapabilitySubscriptionDetail
    ) : SubscriptionRequest {
        override val sourceType = SubscriptionSource.CAPABILITY
    }

    fun toRequest(): com.smartthings.sdk.client.models.SubscriptionRequest {
        return com.smartthings.sdk.client.models.SubscriptionRequest().apply {
            sourceType = this.sourceType

            when (this) {
                is DeviceSubscriptionRequest -> {}
                is CapabilitySubscriptionRequest -> {}
            }
        }
    }
}