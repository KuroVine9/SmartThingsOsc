package com.kuro9.iot.config

import com.smartthings.sdk.smartapp.core.SmartAppDefinition
import com.smartthings.sdk.smartapp.spring.SpringSmartAppDefinition
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SmartThingsConfig {

    @Bean
    fun smartAppDef(applicationContext: ApplicationContext): SmartAppDefinition =
        SpringSmartAppDefinition.of(applicationContext)
}