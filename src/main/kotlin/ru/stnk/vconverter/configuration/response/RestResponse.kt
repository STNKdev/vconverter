package ru.stnk.vconverter.configuration.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class RestResponse {
    var error: Int? = null
    val data: MutableMap<String, Any> = mutableMapOf()

    constructor() {}

    constructor(data: Map<String, Any>) {

    }

    constructor(error: Int, data: Map<String, String>) {
        this.error = error
        this.data.putAll(data)
    }
}