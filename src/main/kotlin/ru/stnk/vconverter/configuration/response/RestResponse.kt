package ru.stnk.vconverter.configuration.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.OK)
@JsonInclude(JsonInclude.Include.NON_NULL)
class RestResponse {
    var error: Int? = null
    val data: MutableMap<String, Any> = mutableMapOf()

    constructor() {}

    constructor(data: Map<String, Any>) {
        this.data.putAll(data)
    }

    constructor(error: Int, data: Map<String, String>) {
        this.error = error
        this.data.putAll(data)
    }
}