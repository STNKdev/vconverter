package ru.stnk.vconverter.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.configuration.exception.MultipartFileEmptyException
import ru.stnk.vconverter.configuration.exception.UuidNotFoundException
import ru.stnk.vconverter.entity.UploadFileStatus
import ru.stnk.vconverter.repository.UploadFileStatusRepository
import ru.stnk.vconverter.storage.FileSystemStorageService
import ru.stnk.vconverter.storage.exception.FileExtensionException
import ru.stnk.vconverter.task.TaskConvertVideo


@Service
class MainControllerService (
        private val storageService: FileSystemStorageService,
        private val taskConvertVideo: TaskConvertVideo,
        private val uploadFileStatusRepository: UploadFileStatusRepository,
        private val treadPoolTaskExecutor: ThreadPoolTaskExecutor
) {

    val logger: Logger = LoggerFactory.getLogger(MainControllerService::class.java)

    fun checkAndSaveFile (file: MultipartFile): String {

        if (file.isEmpty) {
            throw MultipartFileEmptyException()
        }

        // Возвращаемый uuid
        val uuid: String

        logger.debug("Проверка ${file.originalFilename} на допустимое расширение файла")
        when (StringUtils.getFilenameExtension(file.originalFilename)) {
            "mov","avi","wmv","flv","3gp","mp4","mpg" -> uuid = storageService.storeTemp(file)
            else -> throw FileExtensionException()
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
        val uploadFileStatus: UploadFileStatus? = uploadFileStatusRepository.findByUuid(uuid)
        if (uploadFileStatus != null) {
            return uploadFileStatus.status
        } else {
            throw UuidNotFoundException()
        }
    }

    fun downloadResource(uuid: String): Resource? {
        val fileExtention: String? = StringUtils.getFilenameExtension(uuid)
        var uuidWithoutExtension: String = ""
        if (fileExtention != null) {
            uuidWithoutExtension = uuid.replace(".$fileExtention", "")
        }
        val listResource: List<Resource> = storageService.loadAsResourceDownload(uuidWithoutExtension)
        var resource: Resource? = null
        if (listResource.isNotEmpty() && listResource.size == 2) {
            if (listResource[0].filename.equals(uuid, ignoreCase = true)
                    || listResource[0].filename.equals(uuid.replace("jpeg", "jpg"), ignoreCase = true)) {
                resource = listResource[0]
            }
            if (listResource[1].filename.equals(uuid, ignoreCase = true)
                    || listResource[1].filename.equals(uuid.replace("jpeg", "jpg"), ignoreCase = true)) {
                resource = listResource[1]
            }
        }

        logger.debug("<DOWNLOAD>original uuid: $uuid; replace uuid: ${uuid.replace("jpeg", "jpg")}")
        return resource
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