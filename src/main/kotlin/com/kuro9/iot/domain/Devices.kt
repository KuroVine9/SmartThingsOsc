package com.kuro9.iot.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable

@Entity(name = "devices")
@IdClass(Devices.PK::class)
class Devices(
    @Id val deviceId: String,
    @Id val appId: String,
    val componentId: String,
    val locationId: String,

    val internalDeviceId: String,

    var subscriptionId: String?,
) {
    data class PK(
        val deviceId: String = "",
        val appId: String = ""
    ) : Serializable
}



