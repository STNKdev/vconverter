package ru.stnk.vconverter.configuration.exception

import java.lang.RuntimeException

class UuidNotFoundException: RuntimeException {
    constructor() : super(){}
    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}