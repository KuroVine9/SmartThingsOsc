package com.kuro9.iot.service

import com.kuro9.iot.config.AppConfig
import com.kuro9.iot.exception.NotValidElementException
import com.kuro9.iot.utils.errorLog
import com.kuro9.iot.utils.infoLog
import com.kuro9.iot.vo.DeviceCapabilityStatusVo
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SmartThingsApiService(private val config: AppConfig) {
    private val baseUrl = config.smartThingBaseUrl
    private val token = config.smartThingToken

    private val defaultHeader = HttpHeaders().apply {
        setBearerAuth(token)
        accept = listOf(MediaType.APPLICATION_JSON)
    }

    fun deviceStat(deviceId: String, componentId: String, capabilityId: String): DeviceCapabilityStatusVo {
        val url = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/devices/$deviceId/components/$componentId/capabilities/$capabilityId/status")
            .build().toUri()
        infoLog(url.toString())
        val requestEntity: RequestEntity<Map<String, Any>> = RequestEntity(defaultHeader, HttpMethod.GET, url)
        val result = runCatching {
            RestTemplate().exchange(requestEntity, Map::class.java)
        }.onFailure {
            errorLog("Error while Exchange", it)
            if (it !is HttpClientErrorException) throw it
            when (it.statusCode) {
                HttpStatus.UNPROCESSABLE_ENTITY, HttpStatus.BAD_REQUEST -> throw IllegalArgumentException("Not valid Arguments")
                HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED -> throw RuntimeException("Not valid token")
                else -> throw it
            }
        }.getOrThrow()

        val data = result.body?.entries?.firstOrNull() ?: throw NotValidElementException()
        val innerData = data.value as? Map<*, *> ?: throw NotValidElementException("Not expected struct")

        return DeviceCapabilityStatusVo(
            value = innerData.get("value") as? String ?: throw NotValidElementException("valid parse error"),
            timestamp = LocalDateTime.parse(
                innerData.get("timestamp") as? String ?: throw NotValidElementException("timestamp parse error"),
                DateTimeFormatter.ISO_DATE_TIME
            )
        )
    }
}