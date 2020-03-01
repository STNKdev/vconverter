package ru.stnk.vconverter.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.service.MainControllerService
import java.io.File
import java.io.IOException


@RestController
class MainController (
        private val mainService: MainControllerService,
        private val threadPoolTaskExecutor: ThreadPoolTaskExecutor
) {

    val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    @PostMapping("/upload")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile) : ResponseEntity<out Any> {

        val name: String = mainService.checkAndSaveFile(file)

        return ResponseEntity(mapOf("id" to name), HttpStatus.OK)

    }

    @GetMapping("/check")
    fun handleCheckStatus(@RequestParam("id") uuid: String) : ResponseEntity<out Any> {
        return ResponseEntity(mainService.checkStatus(uuid) ,HttpStatus.OK)
    }

    /*@GetMapping("/info-tread")
    fun handleInfoTreadPool(@RequestParam("id") uuid: String) : ResponseEntity<out Any> {
        //val h: Future<out Any> = treadPoolTaskExecutor.submit({println("55555")})
        //threadPoolTaskExecutor.submit( { mainService.taskTread(uuid) } )
        //mainService.runTaskConvertVideo(uuid)
        return ResponseEntity(HttpStatus.OK)
    }*/

    /*@GetMapping("/check-thread")
    fun handleCheckTread() : ResponseEntity<out Any> {
        return ResponseEntity(mapOf("activeCount" to mainService.checkActiveThread()), HttpStatus.OK)
    }*/

}