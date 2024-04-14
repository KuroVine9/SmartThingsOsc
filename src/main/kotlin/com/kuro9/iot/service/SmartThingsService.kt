package com.kuro9.iot.service

import com.kuro9.iot.config.AppConfig
import com.kuro9.iot.vo.DeviceState
import com.kuro9.iot.vo.DeviceStateChangeRequest
import com.kuro9.iot.vo.DeviceStateChangeResponse
import com.smartthings.sdk.client.ApiClient
import com.smartthings.sdk.client.methods.DevicesApi
import com.smartthings.sdk.client.models.CapabilityStatus
import com.smartthings.sdk.client.models.DeviceCommand
import com.smartthings.sdk.client.models.DeviceCommandsRequest
import org.springframework.stereotype.Service
import java.util.*


@Service
class SmartThingsService(
    private val messageTemplate: org.springframework.messaging.simp.SimpMessagingTemplate,
    private val apiClient: ApiClient,
    private val config: AppConfig
) {
    fun broadcast(payload: DeviceStateChangeResponse) = messageTemplate.convertAndSend("/sub", payload)
    fun changeState(payload: DeviceStateChangeRequest) {
        val devicesApi = apiClient.buildClient(DevicesApi::class.java)

        val command = DeviceCommand()
            .capability(payload.capability).command(payload.value).arguments(Collections.emptyList())
        val request = DeviceCommandsRequest().addCommandsItem(command)

        devicesApi.executeDeviceCommands(config.smartThingToken.toBearerString(), payload.deviceId, request)
    }

    fun getDeviceState(payload: DeviceState): CapabilityStatus? {
        val devicesApi = apiClient.buildClient(DevicesApi::class.java)
        return devicesApi.getDeviceStatusByCapability(
            config.smartThingToken.toBearerString(),
            payload.deviceId,
            payload.componentId,
            payload.capability
        )
    }

    private fun String.toBearerString() = "Bearer $this"
}

