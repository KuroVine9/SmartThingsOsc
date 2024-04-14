package com.kuro9.iot.config

import com.kuro9.iot.domain.Devices
import com.kuro9.iot.enumurate.InternalDeviceType
import com.kuro9.iot.repository.IotDeviceRepository
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
import org.springframework.beans.factory.annotation.Qualifier
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
    fun installHandler(deviceRepo: IotDeviceRepository): InstallHandler {
        return InstallHandler { executionRequest: ExecutionRequest ->
            infoLog("INSTALL: executionRequest = $executionRequest")

            val appId = executionRequest.installData.installedApp.installedAppId
            val locationId = executionRequest.installData.installedApp.locationId
            val configMap = executionRequest.installData.installedApp.config

            InternalDeviceType.entries.forEach { type ->
                configMap[type.internalId]?.let {
                    it.forEach { deviceConfig ->
                        with(deviceConfig.deviceConfig) {
                            deviceRepo.save(
                                Devices(
                                    deviceId = deviceId,
                                    appId = appId,
                                    componentId = componentId,
                                    locationId = locationId,
                                    internalDeviceId = type.internalId,
                                )
                            )
                        }
                    }
                }

            }

            Response.ok(InstallResponseData())
        }
    }

    @Bean
    fun uninstallHandler(deviceRepo: IotDeviceRepository): UninstallHandler {
        return UninstallHandler { executionRequest: ExecutionRequest ->
            infoLog("UNINSTALL: executionRequest = $executionRequest")

            val appId = executionRequest.uninstallData.installedApp.installedAppId

            deviceRepo.findAllByAppId(appId).forEach {
                infoLog("Deleting device: $it")
                deviceRepo.delete(it)
            }

            Response.ok(UninstallResponseData())
        }
    }

    @Bean
    fun updateHandler(deviceRepo: IotDeviceRepository): UpdateHandler {
        // The update lifecycle event is called when the user updates
        // configuration options previously set via the install lifecycle
        // event so this should be similar to that handler.
        return UpdateHandler { executionRequest: ExecutionRequest ->
            infoLog("UPDATE: executionRequest = $executionRequest")

            infoLog("Deleting Exist Devices...")

            val appId = executionRequest.updateData.installedApp.installedAppId
            val locationId = executionRequest.updateData.installedApp.locationId
            val configMap = executionRequest.updateData.installedApp.config

            deviceRepo.findAllByAppId(appId).forEach {
                deviceRepo.delete(it)
            }

            infoLog("Saving New Devices...")
            InternalDeviceType.entries.forEach { type ->
                configMap[type.internalId]?.let {
                    it.forEach { deviceConfig ->
                        with(deviceConfig.deviceConfig) {
                            deviceRepo.save(
                                Devices(
                                    deviceId = deviceId,
                                    appId = appId,
                                    componentId = componentId,
                                    locationId = locationId,
                                    internalDeviceId = type.internalId,
                                )
                            )
                        }
                    }
                }

            }


            Response.ok(UpdateResponseData())
        }
    }

    @Bean(name = ["initializeResponse"])
    fun initializeResponse(): ConfigurationResponseData {
        return ConfigurationResponseData().apply {
            initialize = InitializeSetting().apply {
                name = "SmartThingsOsc"
                description = "SmartThings custom Controller"
                id = "app"
                permissions = listOf("r:devices:*", "w:devices:*", "x:devices:*")
                firstPageId = "1"
            }
        }
    }

    @Bean(name = ["pageResponse"])
    fun pageResponse(): ConfigurationResponseData {
        return ConfigurationResponseData().apply {
            page = Page().apply {
                pageId = "1"
                name = "SmartThingsOsc"
                nextPageId = null
                previousPageId = null
                isComplete = true
                sections = InternalDeviceType.entries.map { it.toSection() }
            }
        }
    }

    @Bean
    fun configurationHandler(
        @Qualifier("initializeResponse") initalizeResponse: ConfigurationResponseData,
        @Qualifier("pageResponse") pageResponse: ConfigurationResponseData,
    ): ConfigurationHandler {
        return ConfigurationHandler { executionRequest: ExecutionRequest ->
            infoLog("CONFIGURATION: executionRequest = $executionRequest")

            return@ConfigurationHandler when (executionRequest.configurationData.phase) {
                ConfigurationPhase.INITIALIZE -> Response.ok(initalizeResponse)
                ConfigurationPhase.PAGE -> Response.ok(pageResponse)
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

                        null -> return@EventHandler Response.status(400)
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