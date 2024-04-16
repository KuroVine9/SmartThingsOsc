package com.kuro9.iot.service

import com.kuro9.iot.config.AppConfig
import com.kuro9.iot.domain.Devices
import com.kuro9.iot.repository.IotDeviceRepository
import com.kuro9.iot.vo.AppSubscriptionRequest
import com.kuro9.iot.vo.DeviceState
import com.kuro9.iot.vo.DeviceStateChangeRequest
import com.kuro9.iot.vo.DeviceStateChangeResponse
import com.smartthings.sdk.client.ApiClient
import com.smartthings.sdk.client.methods.DevicesApi
import com.smartthings.sdk.client.methods.SubscriptionsApi
import com.smartthings.sdk.client.models.CapabilityStatus
import com.smartthings.sdk.client.models.DeviceCommand
import com.smartthings.sdk.client.models.DeviceCommandsRequest
import com.smartthings.sdk.client.models.Subscription
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class SmartThingsService(
    private val messageTemplate: org.springframework.messaging.simp.SimpMessagingTemplate,
    private val apiClient: ApiClient,
    private val config: AppConfig,
    private val deviceRepo: IotDeviceRepository
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

    @Transactional(rollbackFor = [Exception::class])
    fun createSubscription(appId: String, request: AppSubscriptionRequest): Subscription {
        val subscriptionApi = apiClient.buildClient(SubscriptionsApi::class.java)
        val result = subscriptionApi.saveSubscription(
            appId,
            config.smartThingToken.toBearerString(),
            request.toRequest()
        )

        if (result.id == null) throw IllegalStateException("Subscription Id is null!")

        if (request is AppSubscriptionRequest.CapabilitySubscriptionRequest) return result

        val deviceRequest = request as AppSubscriptionRequest.DeviceSubscriptionRequest
        val deviceObj = deviceRepo.findById(Devices.PK(deviceRequest.device.deviceId, appId))
        deviceObj.get().subscriptionId = result.id

        return result
    }

    fun deleteSubscription(appId: String, request: AppSubscriptionRequest): Subscription {
        val subscriptionsApi = apiClient.buildClient(SubscriptionsApi::class.java)
        subscriptionsApi.deleteSubscription()
    }

    private fun String.toBearerString() = "Bearer $this"
}

