package ru.stnk.vconverter.configuration

import io.undertow.server.RequestTooBigException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MultipartException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.stnk.vconverter.configuration.response.RestResponse
import ru.stnk.vconverter.storage.exception.FileExtensionException
import ru.stnk.vconverter.storage.exception.FileNotFoundException
import ru.stnk.vconverter.storage.exception.StorageException

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    override fun handleExceptionInternal(ex: Exception,
                                         @Nullable body: Any?,
                                         headers: HttpHeaders,
                                         status: HttpStatus,
                                         request: WebRequest)
            : ResponseEntity<Any> {
        return ResponseEntity(RestResponse(500, mapOf("description" to "Внутренняя ошибка сервиса")), HttpStatus.OK)
    }

    override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException,
                                              headers: HttpHeaders,
                                              status: HttpStatus,
                                              request: WebRequest)
            : ResponseEntity<Any> {
        return ResponseEntity(RestResponse(status.value(), mapOf("description" to ex.localizedMessage)), HttpStatus.OK)
    }

    @ExceptionHandler(FileExtensionException::class)
    fun handlerFileExtensionException(ex: FileExtensionException,
                                      request: WebRequest)
            :ResponseEntity<RestResponse> {
        return ResponseEntity(RestResponse(150, mapOf("description" to "Недопустимый формат файла")), HttpStatus.OK)
    }

    @ExceptionHandler(FileNotFoundException::class)
    fun handlerFileNotFoundException(ex: FileNotFoundException,
                                     request: WebRequest)
            :ResponseEntity<RestResponse> {
        return ResponseEntity(RestResponse(154, mapOf("description" to ex.localizedMessage)), HttpStatus.OK)
    }

    @ExceptionHandler(StorageException::class)
    fun handlerStorageException(ex: StorageException,
                                request: WebRequest)
            :ResponseEntity<RestResponse> {
        return ResponseEntity(RestResponse(154, mapOf("description" to "Файл не найден")), HttpStatus.OK)
    }

}