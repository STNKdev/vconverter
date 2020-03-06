package ru.stnk.vconverter.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.configuration.response.RestResponse
import ru.stnk.vconverter.service.MainControllerService


@RestController
class MainController (
        private val mainService: MainControllerService
) {

    val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    @PostMapping("/upload")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile) : RestResponse {

        val name: String = mainService.checkAndSaveFile(file)

        return RestResponse(mapOf("id" to name))

    }

    @GetMapping("/check")
    fun handleCheckStatus(@RequestParam("id") uuid: String) : RestResponse {
        return RestResponse(mapOf("status" to mainService.checkStatus(uuid)))
    }

    // тут по идее должно работать ".+\\.(mp4|jpg|jpeg)"
    // но в этом случае выбрасывается исключение:
    /*
    * Request processing failed; nested exception is java.lang.IllegalArgumentException: The number of capturing groups in the pattern segment (.+\\.(mp4|jpg|jpeg)) does not match the number of URI template variables it defines, which can occur if capturing groups are used in a URI template regex. Use non-capturing groups instead.
    *
    * */
    @GetMapping("/download/{uuid:.+\\.mp4|.+\\.jpg|.+\\.jpeg}")
    @ResponseBody
    fun handleFileDownload(@PathVariable uuid: String): ResponseEntity<Resource> {
        val downloadResource: Resource? = mainService.downloadResource(uuid)
        logger.debug("<DOWNLOAD> Файл для загрузки: ${downloadResource.toString()} , имя файла: ${downloadResource?.filename}")
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + downloadResource?.filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(downloadResource)
    }

    /*@GetMapping("/info-tread")
    fun handleInfoTreadPool(@RequestParam("id") uuid: String) : ResponseEntity<out Any> {
        //val h: Future<out Any> = treadPoolTaskExecutor.submit({println("55555")})
        //threadPoolTaskExecutor.submit( { mainService.taskTread(uuid) } )
        //mainService.runTaskConvertVideo(uuid)
        return ResponseEntity(HttpStatus.OK)
    }*/

    @GetMapping("/check-thread")
    fun handleCheckTread() : ResponseEntity<out Any> {
        return ResponseEntity(mapOf("activeThread" to mainService.checkActiveThread()), HttpStatus.OK)
    }

}