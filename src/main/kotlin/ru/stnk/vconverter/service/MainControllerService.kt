package ru.stnk.vconverter.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.entity.UploadFileData
import ru.stnk.vconverter.repository.UploadFileDataRepository
import ru.stnk.vconverter.storage.FileSystemStorageService
import ru.stnk.vconverter.storage.exception.FileExtentionException
import ru.stnk.vconverter.task.TaskConvertVideo


@Service
class MainControllerService (
        private val storageService: FileSystemStorageService,
        private val taskConvertVideo: TaskConvertVideo,
        private val uploadFileDataRepository: UploadFileDataRepository,
        private val treadPoolTaskExecutor: ThreadPoolTaskExecutor
) {

    val logger: Logger = LoggerFactory.getLogger(MainControllerService::class.java)

    fun checkAndSaveFile (file: MultipartFile): String {

        val uuid: String

        logger.debug("Проверка ${file.originalFilename} на допустимое расширение файла")
        when (StringUtils.getFilenameExtension(file.originalFilename)) {
            "mov","avi","wmv","flv","3gp","mp4","mpg" -> uuid = storageService.storeTemp(file)
            else -> throw FileExtentionException("Недопустимый формат файла")
        }

        // Блокирует поток и ждёт выполнения задачи, можно получить результат
        //val taskRun: CompletableFuture<Boolean> = taskConvertVideo(uuid)
        checkActiveThread()
        logger.debug("Запуск задачи на переконвертирование $uuid")
        // Неблокирует поток, задача выполняет в фоне в отдельном потоке
        taskConvertVideo.runTask(uuid)
        checkActiveThread()

        return uuid
    }

    fun checkActiveThread() {
        val activeThread = treadPoolTaskExecutor.activeCount
        logger.debug("Активные потоки: $activeThread")
    }

    fun checkStatus(uuid: String): String {
        val uploadFileData: UploadFileData? = uploadFileDataRepository.findByUuid(uuid)
        if (uploadFileData != null) {
            return uploadFileData.status
        } else {
            return "Неверный идентификатор"
        }
    }

    /*fun taskTread(uuid: String) {

        val file: Path = storageService.loadTemp(uuid)

        val fileName = file.fileName
        logger.debug(fileName.toString())

        try {
            val processBuilder = ProcessBuilder()
            val isWindows: Boolean = System.getProperty("os.name").toLowerCase().startsWith("windows")
            logger.debug(System.getProperty("os.name").toLowerCase())

            if (isWindows) {
                processBuilder.command("cmd.exe", "/c", "ffmpeg", "-i", "tmp\\${fileName.toString()}", "-s", "426x240", "download\\$uuid.mp4")
            } else {
                processBuilder.command("sh", "-c", "ls")
            }

            processBuilder.directory(File("./"))

            val process = processBuilder.start()
            //val streamGobbler = StreamGobbler(process.inputStream, Consumer {x: String? -> println(x)})
            //treadPoolTaskExecutor.submit(streamGobbler)
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val linesErr: List<String> = errorReader.readLines()
            for (line in linesErr) {
                println(line)
            }

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val lines: List<String> = reader.readLines()
            for (ln in lines) {
                println(ln)
            }

            val exitCode = process.waitFor()
            logger.debug("Tread: ${Thread.currentThread()} -> Exited code with: $exitCode")

        } catch (e: IOException) {
            logger.debug(e.message)
        } catch (e: InterruptedException) {
            logger.debug(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val count = treadPoolTaskExecutor.activeCount
        //Thread.sleep(10000)
        println("Mission complete, activeCount: $count")
    }

    private class StreamGobbler(
            private val inputStream: InputStream,
            private val consumer: Consumer<String>
    ) : Runnable {
        override fun run() {
            BufferedReader(InputStreamReader(inputStream)).lines()
                    .forEach(consumer)
        }

    }

    *//*fun runTaskConvertVideo (uuid: String) {
        val task: Future<List<String>> = treadPoolTaskExecutor.submit({ taskConvertVideo(uuid) })
        if (task.isDone) {
            val paths: Path = Paths.get(storageService.downloadDir.resolve("$uuid.mp4").toUri())
            logger.debug("Задача выполенена с путём ${paths.toString()}")
        }

    }*//*

    private class ProcessReadTask(
            private val inputStream: InputStream
    ) : Callable<List<String>> {
        override fun call(): List<String> {
            return BufferedReader(InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.toList())
        }
    }*/

}