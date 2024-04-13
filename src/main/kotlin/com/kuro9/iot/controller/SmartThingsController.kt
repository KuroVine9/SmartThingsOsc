package com.kuro9.iot.controller

import com.kuro9.iot.utils.infoLog
import com.smartthings.sdk.smartapp.core.SmartApp
import com.smartthings.sdk.smartapp.core.SmartAppDefinition
import com.smartthings.sdk.smartapp.core.extensions.HttpVerificationService
import com.smartthings.sdk.smartapp.core.models.AppLifecycle
import com.smartthings.sdk.smartapp.core.models.ExecutionRequest
import com.smartthings.sdk.smartapp.core.models.ExecutionResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors


@RestController
class SmartThingsController(
    smartAppDef: SmartAppDefinition
    ) {
    private val smartApp = SmartApp.of(smartAppDef)

    @GetMapping("/")
    fun home(): String {
        infoLog("Received request for home")
        return "This app only functions as a SmartThings Automation webhook endpoint app"
    }

    @PostMapping("/smartapp")
    fun handle(@RequestBody executionRequest: ExecutionRequest, request: HttpServletRequest): ExecutionResponse {

        return smartApp.execute(executionRequest)
    }
}