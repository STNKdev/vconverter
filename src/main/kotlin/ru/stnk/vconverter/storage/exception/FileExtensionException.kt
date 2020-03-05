package ru.stnk.vconverter.storage.exception

import java.lang.RuntimeException

class FileExtensionException: RuntimeException {
    constructor() : super(){}
    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}