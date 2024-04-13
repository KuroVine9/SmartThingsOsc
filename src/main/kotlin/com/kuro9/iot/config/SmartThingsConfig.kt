package com.kuro9.iot.config

import com.kuro9.iot.utils.infoLog
import com.smartthings.sdk.client.ApiClient
import com.smartthings.sdk.smartapp.core.Response
import com.smartthings.sdk.smartapp.core.SmartAppDefinition
import com.smartthings.sdk.smartapp.core.extensions.*
import com.smartthings.sdk.smartapp.core.internal.handlers.DefaultPingHandler
import com.smartthings.sdk.smartapp.core.models.*
import com.smartthings.sdk.smartapp.core.service.TokenRefreshService
import com.smartthings.sdk.smartapp.core.service.TokenRefreshServiceImpl
import com.smartthings.sdk.smartapp.spring.SpringSmartAppDefinition
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SmartThingsConfig {
    // We don't need to include a PingHandler because the default is sufficient.
    // This is included here to help with debugging a new SmartApp.
    @Bean
    fun pingHandler(): PingHandler {
        return object : DefaultPingHandler() {
            @Throws(Exception::class)
            override fun handle(executionRequest: ExecutionRequest): ExecutionResponse {
                infoLog("PING: executionRequest = $executionRequest")
                return super.handle(executionRequest)
            }
        }
    }

    // In a real application (and perhaps even a future version of this
    // example), some of these simple handlers might be more complicated and it
    // might make sense to move them out into their own classes tagged with
    // @Component.
    // 여기서 deviceId 및 capabilityId 저장
    @Bean
    fun installHandler(): InstallHandler {
        return InstallHandler { executionRequest: ExecutionRequest ->
            infoLog("INSTALL: executionRequest = $executionRequest")

            executionRequest.installData.installedApp.config["testsoundsensorid"]?.forEach {
                infoLog("deviceInfo={}", it.deviceConfig) // TODO 디바이스 정보 저장
            }


            Response.ok(InstallResponseData())
        }
    }

    @Bean
    fun updateHandler(): UpdateHandler {
        // The update lifecycle event is called when the user updates
        // configuration options previously set via the install lifecycle
        // event so this should be similar to that handler.
        return UpdateHandler { executionRequest: ExecutionRequest ->
            infoLog("UPDATE: executionRequest = $executionRequest")
            Response.ok(UpdateResponseData())
        }
    }

    @Bean
    fun configurationHandler(): ConfigurationHandler {
        return ConfigurationHandler { executionRequest: ExecutionRequest ->
            infoLog("CONFIGURATION: executionRequest = $executionRequest")

            return@ConfigurationHandler when (executionRequest.configurationData.phase) {
                ConfigurationPhase.INITIALIZE -> Response.ok(ConfigurationResponseData().apply {
                    initialize = InitializeSetting().apply {
                        name = "webhooktest"
                        description = "helpme im trapped in a webhook factory"
                        id = "webhooktest"
                        permissions = listOf("r:devices:*")
                        firstPageId = "1"
                    }
                })

                ConfigurationPhase.PAGE -> Response.ok(ConfigurationResponseData().apply {
                    page = Page().apply {
                        pageId = "1"
                        name = "webhooktest"
                        nextPageId = null
                        previousPageId = null
                        isComplete = true
                        sections = listOf(
                            Section().apply {
                                name = "sensortest"
                                settings = listOf(
                                    DeviceSetting().apply {
                                        id = "testsoundsensorid"
                                        name = "sensortest"
                                        type = SettingType.DEVICE
                                        description = "sound"
                                        isRequired = true
                                        isMultiple = false
                                        capabilities = listOf(
                                            "audioVolume"
                                        )
                                        permissions = listOf(
                                            DeviceSetting.PermissionsEnum.R
                                        )

                                    }
                                )
                            }
                        )
                    }
                })

                null -> Response.status(400)
            }
        }
    }

    @Bean
    fun eventHandler(): EventHandler {
        return EventHandler { executionRequest: ExecutionRequest ->
            infoLog("EVENT: executionRequest = $executionRequest")

            executionRequest.eventData?.let {
                it.events.forEach { event ->
                    when (event.eventType) {
                        EventType.DEVICE_EVENT -> {
                            infoLog(event.deviceEvent.toString())
                        }

                        EventType.MODE_EVENT -> {
                            infoLog(event.modeEvent.toString())
                        }

                        EventType.TIMER_EVENT -> {
                            infoLog(event.timerEvent.toString())
                        }

                        EventType.DEVICE_COMMANDS_EVENT -> {
                            infoLog(event.deviceCommandsEvent.toString())
                        }
                    }
                }
            } ?: IllegalStateException("eventData is null")

            Response.ok(EventResponseData())
        }
    }

    @Bean
    fun httpClient(): CloseableHttpClient {
        return HttpClients.createDefault()
    }

    @Bean
    fun apiClient(): ApiClient {
        return ApiClient()
    }

    @Bean
    fun httpVerificationService(): HttpVerificationService {
        return HttpVerificationService(httpClient())
    }

    @Bean
    fun tokenRefreshService(
        config: AppConfig
    ): TokenRefreshService {
        return TokenRefreshServiceImpl(
            config.smartThingsOAuthClientId,
            config.smartThingsOAuthClientSecret,
            httpClient()
        )
    }


    @Bean
    fun smartAppDef(applicationContext: ApplicationContext): SmartAppDefinition =
        SpringSmartAppDefinition.of(applicationContext)
}