package ru.stnk.vconverter.task

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import ru.stnk.vconverter.entity.DownloadFileData
import ru.stnk.vconverter.storage.FileSystemStorageService
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

@Service
class TaskConvertVideo (
        val storageService: FileSystemStorageService
) {

    private val logger: Logger = LoggerFactory.getLogger(TaskConvertVideo::class.java)

    @Async
    fun runTask(uuid: String): CompletableFuture<Boolean> {

        val file: Path = storageService.loadTemp(uuid)

        val fileName = file.fileName
        logger.debug(fileName.toString())

        storageService.changeStatus(uuid, "Обрабатывается")

        val pathDirectoryUUIDDownload: Path = storageService.createDirectoryUUIDDownload(uuid)
        val downloadFileData = DownloadFileData()
        downloadFileData.uuid = uuid
        downloadFileData.directoryName = pathDirectoryUUIDDownload.toString()
        downloadFileData.pathVideoFile = pathDirectoryUUIDDownload.resolve("$uuid.mp4").toString()
        downloadFileData.pathImageFile = pathDirectoryUUIDDownload.resolve("$uuid.jpg").toString()
        logger.debug(downloadFileData.toString())

        val processBuilder = ProcessBuilder()
        val isWindows: Boolean = System.getProperty("os.name").toLowerCase().startsWith("windows")
        logger.debug(System.getProperty("os.name").toLowerCase())

        //val commandWindows: List<String> = listOf("cmd.exe", "/c", "ffmpeg", "-hide_banner", "-i", file.toString(), "-s", "426x240", "${storageService.downloadDir.resolve("$uuid.mp4")}")
        val commandWindowsConvertVideo: List<String> = listOf("cmd.exe", "/c", "ffmpeg", "-hide_banner", "-i", file.toString(), "-s", "426x240", downloadFileData.pathVideoFile)
        val commandLinuxConvertVideo: List<String> = listOf("sh", "-c", "ffmpeg", "-hide_banner", "-i", file.toString(), "-s", "426x240", downloadFileData.pathVideoFile)

        if (isWindows) {
            processBuilder.command(commandWindowsConvertVideo)
        } else {
            processBuilder.command(commandLinuxConvertVideo)
        }

        processBuilder.directory(File("./"))

        try {

            val processConvertVideo: Process = processBuilder.start()

            // Используется этот поток ¯\_(ツ)_/¯
            val readerConvertVideoError = BufferedReader(InputStreamReader(processConvertVideo.errorStream))
            val linesConvertVideoErr: List<String> = readerConvertVideoError.readLines()
            var miniInfo: List<String> = emptyList()
            for (line in linesConvertVideoErr) {
                if (line.trim().startsWith("Duration:")) {
                    miniInfo = line.trim().split(", ")
                }
            }

            val readerConvertVideo = BufferedReader(InputStreamReader(processConvertVideo.inputStream))
            val linesConvertVideo: List<String> = readerConvertVideo.readLines()
            //logger.debug(lines.toString())
            /*for (line in lines) {

            }*/

            val exitCodeConvertVideo: Int = processConvertVideo.waitFor()
            logger.debug("Tread: ${Thread.currentThread()} : RunTask with uuid: $uuid -> Exited code with: $exitCodeConvertVideo")

            // ffmpeg -hide_banner -ss 235 -i "Робот для РОБОСУМО NXT.mp4" -vframes 1 -an 1/thumbnail.jpg
            val durationStr = miniInfo[0].removePrefix("Duration: ").trim()
            val fullDurationInSeconds = durationStr.substring(0..1).toInt()*60*60 + durationStr.substring(3..4).toInt()*60 + durationStr.substring(6..7).toInt()

            val commandWindowsThumbnail: List<String> = listOf("cmd.exe", "/c", "ffmpeg", "-hide_banner", "-ss", (fullDurationInSeconds/2).toString(), "-i", file.toString(), "-vframes", "1", "-an", downloadFileData.pathImageFile)
            val commandLinuxThumbnail: List<String> = listOf("sh", "-c", "ffmpeg", "-hide_banner", "-ss", (fullDurationInSeconds/2).toString(), "-i", file.toString(), "-vframes", "1", "-an", downloadFileData.pathImageFile)

            if (isWindows) {
                processBuilder.command(commandWindowsThumbnail)
            } else {
                processBuilder.command(commandLinuxThumbnail)
            }

            val processThumbnail: Process = processBuilder.start()

            val readerThumbnailError = BufferedReader(InputStreamReader(processConvertVideo.errorStream))
            val linesThumbnailErr: List<String> = readerThumbnailError.readLines()

            val readerThumbnail = BufferedReader(InputStreamReader(processConvertVideo.inputStream))
            val linesThumbnail: List<String> = readerThumbnail.readLines()

            val exitCodeProcessThumbnail: Int = processThumbnail.waitFor()
            logger.debug("Tread: ${Thread.currentThread()} : RunTask with uuid: $uuid -> Exited code with: $exitCodeProcessThumbnail")

            if (exitCodeConvertVideo == 0 && exitCodeProcessThumbnail == 0) {
                storageService.storeDownload(downloadFileData)
                storageService.deleteFileTemp(uuid)
            } else {
                logger.debug("Задача не выполенена или выполенена с ошибками $uuid")
            }

        }
        catch (e: IOException) {
            logger.debug(e.message)
        }
        catch (e: InterruptedException) {
            throw IllegalStateException("Задание было прервано", e);
        }

        return CompletableFuture.completedFuture(true)
    }
}