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
import java.nio.file.Paths
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

        //storageService.changeStatus(uuid, "1")

        // Создаём директорию для хранения файлов
        val pathDirectoryUUIDDownload: Path = storageService.createDirectoryUUIDDownload(uuid)

        val downloadFileData = DownloadFileData()
        downloadFileData.uuid = uuid
        downloadFileData.directoryName = pathDirectoryUUIDDownload.toString()
        downloadFileData.pathVideoFile = pathDirectoryUUIDDownload.resolve("$uuid.mp4").toString()
        downloadFileData.pathImageFile = pathDirectoryUUIDDownload.resolve("$uuid.jpg").toString()
        logger.debug(downloadFileData.toString())

        storageService.changeStatus(downloadFileData.uuid, "1")

        val processBuilderVideoConverter = ProcessBuilder()
        val isWindows: Boolean = System.getProperty("os.name").toLowerCase().startsWith("windows")
        logger.debug(System.getProperty("os.name").toLowerCase())

        //val commandWindows: List<String> = listOf("cmd.exe", "/c", "ffmpeg", "-hide_banner", "-i", file.toString(), "-s", "426x240", "${storageService.downloadDir.resolve("$uuid.mp4")}")
        val commandWindowsConvertVideo: List<String> = listOf("cmd.exe", "/c", "ffmpeg", "-hide_banner", "-i", file.toString(), "-s", "426x240", downloadFileData.pathVideoFile)
        val commandLinuxConvertVideo: List<String> = listOf("sh", "-c", "ffmpeg", "-hide_banner", "-i", file.toString(), "-s", "426x240", downloadFileData.pathVideoFile)

        if (isWindows) {
            processBuilderVideoConverter.command(commandWindowsConvertVideo)
        } else {
            processBuilderVideoConverter.command(commandLinuxConvertVideo)
        }

        processBuilderVideoConverter.directory(File("./"))

        try {

            val processConvertVideo: Process = processBuilderVideoConverter.start()

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
            if (linesConvertVideo.isNotEmpty()) {
                for (line in linesConvertVideo) {
                    println(line)
                }
            }

            val exitCodeConvertVideo: Int = processConvertVideo.waitFor()
            logger.debug("Tread: ${Thread.currentThread()} : RunTask with uuid: $uuid -> Exited code process Convert Video with: $exitCodeConvertVideo")

            // Вычисляем продолжительность видео для создания превью из середины
            // ffmpeg -hide_banner -ss 235 -i "Робот для РОБОСУМО NXT.mp4" -vframes 1 -an 1/thumbnail.jpg
            val durationStr = miniInfo[0].removePrefix("Duration: ").trim()
            val fullDurationInSeconds = durationStr.substring(0..1).toInt()*60*60 + durationStr.substring(3..4).toInt()*60 + durationStr.substring(6..7).toInt()

            // Создаём новый процесс для превьюшки

            val processBuilderThumbnail = ProcessBuilder()

            val commandWindowsThumbnail: List<String> = listOf("cmd.exe", "/c", "ffmpeg", "-hide_banner", "-ss", (fullDurationInSeconds/2).toString(), "-i", file.toString(), "-vframes", "1", "-an", downloadFileData.pathImageFile)
            val commandLinuxThumbnail: List<String> = listOf("sh", "-c", "ffmpeg", "-hide_banner", "-ss", (fullDurationInSeconds/2).toString(), "-i", file.toString(), "-vframes", "1", "-an", downloadFileData.pathImageFile)

            if (isWindows) {
                processBuilderThumbnail.command(commandWindowsThumbnail)
            } else {
                processBuilderThumbnail.command(commandLinuxThumbnail)
            }

            processBuilderThumbnail.directory(File("./"))

            val processThumbnail: Process = processBuilderThumbnail.start()

            val readerThumbnailError = BufferedReader(InputStreamReader(processThumbnail.errorStream))
            val linesThumbnailErr: List<String> = readerThumbnailError.readLines()
            for (line in linesThumbnailErr) {
                println(line)
            }

            val readerThumbnail = BufferedReader(InputStreamReader(processThumbnail.inputStream))
            val linesThumbnail: List<String> = readerThumbnail.readLines()
            if (linesThumbnail.isNotEmpty()) {
                for (line in linesThumbnail) {
                    println(line)
                }
            }

            val exitCodeProcessThumbnail: Int = processThumbnail.waitFor()
            logger.debug("Tread: ${Thread.currentThread()} : RunTask with uuid: $uuid -> Exited code process create thumbnail with: $exitCodeProcessThumbnail")

            logger.debug("Проверяем условие: exitCodeConvertVideo = $exitCodeConvertVideo; exitCodeProcessThumbnail = $exitCodeProcessThumbnail")
            if (exitCodeConvertVideo == 0 && exitCodeProcessThumbnail == 0) {
                storageService.storeDownload(downloadFileData)
                storageService.changeStatus(downloadFileData.uuid, "2", downloadFileData.directoryName)
                // Тут происходит что-то невнятное, файлы не удаляются
                storageService.deleteFileTemp(uuid)

                logger.debug("Задача $uuid завершена")
            } else {
                logger.debug("Задача не выполенена или выполенена с ошибками $uuid")
            }

        }
        catch (e: IOException) {
            throw IllegalStateException("Задание было прервано", e)
        }
        catch (e: InterruptedException) {
            throw IllegalStateException("Задание было прервано", e)
        }

        return CompletableFuture.completedFuture(true)
    }
}