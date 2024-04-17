package com.kuro9.iot.vo

import com.smartthings.sdk.client.models.AttributeState

data class DeviceStateChangeResponse(
    val capability: String,
    val componentId: String,
    val deviceId: String,
    val internalId: String,
    val state: AttributeState
)
