package ru.stnk.vconverter.storage.exception

import java.lang.RuntimeException

class FileExtentionException: RuntimeException {
    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}