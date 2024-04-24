package com.kuro9.iot.interceptor

import com.kuro9.iot.utils.infoLog
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

@Component
class WebSocketInterceptor : HandshakeInterceptor{
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val servletReq = (request as ServletServerHttpRequest).servletRequest
        val clientIp = servletReq.remoteAddr

        return (clientIp.startsWith("192.168") || clientIp.startsWith("172.30") || clientIp.startsWith("localhost")).also {
            infoLog("HandShaking with ip=$clientIp ... result=$it")
        }
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // do nothing
    }
}