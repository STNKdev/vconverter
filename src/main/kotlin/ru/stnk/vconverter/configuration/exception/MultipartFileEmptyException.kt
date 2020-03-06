package ru.stnk.vconverter.configuration.exception

class MultipartFileEmptyException: RuntimeException {
    constructor() : super(){}
    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}