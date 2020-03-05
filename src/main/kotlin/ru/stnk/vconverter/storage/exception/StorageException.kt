package ru.stnk.vconverter.storage.exception

import java.lang.RuntimeException

open class StorageException: RuntimeException {

    constructor(): super() {}

    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}