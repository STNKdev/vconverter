package ru.stnk.vconverter.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.service.UploadControllerService


@RestController
class UploadController (
        private val uploadService: UploadControllerService
) {

    @PostMapping("/upload")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile) : ResponseEntity<out Any> {

        val name: String = uploadService.checkAndSaveFile(file)

        return ResponseEntity(mapOf("id" to name), HttpStatus.OK)

    }
}