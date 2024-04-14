package com.kuro9.iot.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class UnauthorizedException(message: String = "Not valid request") : RuntimeException(message)