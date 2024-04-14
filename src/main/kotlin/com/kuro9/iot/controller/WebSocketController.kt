package com.kuro9.iot.controller

import com.kuro9.iot.service.SmartThingsService
import com.kuro9.iot.utils.infoLog
import com.kuro9.iot.vo.DeviceState
import com.kuro9.iot.vo.DeviceStateChangeRequest
import com.kuro9.iot.vo.DeviceStateChangeResponse
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class WebSocketController(private val smartThingsService: SmartThingsService) {

    // destination: /pub/request
    @MessageMapping("/request")
    fun stateChangeRequest(body: DeviceStateChangeRequest) {
        infoLog("Received state change request: $body")

        // webSocketService.changeState(body)
        val state = smartThingsService.getDeviceState(
            DeviceState(
                body.capability,
                body.componentId,
                body.deviceId
            )
        )

        infoLog("State after change: $state")

        state?.let {
            smartThingsService.broadcast(
                DeviceStateChangeResponse(
                    body.capability,
                    body.componentId,
                    body.deviceId,
                    it.values.first()
                )
            )
        }
    }
}