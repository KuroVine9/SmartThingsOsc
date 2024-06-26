package com.kuro9.iot.vo

data class DeviceStateChangeRequest(
    val capability: String,
    val componentId: String,
    val deviceId: String,
    val subscriptionId: String,
    val value: String
)
