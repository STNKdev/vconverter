package ru.stnk.vconverter.storage.exception

/*import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus*/


//@ResponseStatus(HttpStatus.NOT_FOUND)
class FileNotFoundException: StorageException {

    constructor(): super() {}

    constructor(message: String) : super (message) {}

    constructor(message: String, cause: Throwable) : super (message, cause) {}
}