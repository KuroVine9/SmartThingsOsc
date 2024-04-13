package com.kuro9.iot.controller

import com.kuro9.iot.service.SmartThingsApiService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(private val api: SmartThingsApiService) {

    @GetMapping("test")
    fun test(
        @RequestParam deviceId: String,
        @RequestParam componentId: String,
        @RequestParam capabilityId: String
    ): String {
        return api.deviceStat(
            deviceId, componentId, capabilityId
        ).toString()
    }
}