package com.kuro9.iot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "app")
data class AppConfig(
    val oscRxPort: Int,
    val oscTxPort: Int,

    val smartThingToken: String,
    val smartThingBaseUrl: String,
)