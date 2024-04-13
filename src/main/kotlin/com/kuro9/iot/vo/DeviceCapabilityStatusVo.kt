package com.kuro9.iot.vo

import java.time.LocalDateTime


data class DeviceCapabilityStatusVo(
    val value: String,
    val timestamp: LocalDateTime
)

fun Map<*, *>.toVo() {

}
