package com.kuro9.iot

import com.kuro9.iot.config.AppConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@SpringBootApplication
@EnableConfigurationProperties(value = [AppConfig::class])
class SmartThingsOscApplication

fun main(args: Array<String>) {
    runApplication<SmartThingsOscApplication>(*args)
}
