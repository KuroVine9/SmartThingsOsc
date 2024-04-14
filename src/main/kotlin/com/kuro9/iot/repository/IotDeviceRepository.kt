package com.kuro9.iot.repository

import com.kuro9.iot.domain.Devices
import org.springframework.data.jpa.repository.JpaRepository

interface IotDeviceRepository : JpaRepository<Devices, Devices.PK> {
    fun findAllByInternalDeviceId(internalDeviceId: String): List<Devices>
    fun findAllByAppId(appId: String): List<Devices>
}