package ru.stnk.vconverter.storage

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.entity.DownloadFileData
import ru.stnk.vconverter.entity.UploadFileData
import ru.stnk.vconverter.entity.UploadFileStatus
import ru.stnk.vconverter.repository.DownloadFileDataRepository
import ru.stnk.vconverter.repository.UploadFileDataRepository
import ru.stnk.vconverter.repository.UploadFileStatusRepository
import ru.stnk.vconverter.storage.exception.FileNotFoundException
import ru.stnk.vconverter.storage.exception.StorageException
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Stream
import javax.annotation.PostConstruct


@Service
class FileSystemStorageService(
        private val properties: StorageProperties,
        private val uploadFileDataRepository: UploadFileDataRepository,
        private val uploadFileStatusRepository: UploadFileStatusRepository,
        private val downloadFileDataRepository: DownloadFileDataRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(FileSystemStorageService::class.java)

    val tempDir: Path = Paths.get(properties.temp)

    val downloadDir: Path = Paths.get(properties.download)

    private val EXTENSION_SEPARATOR = '.'

    @PostConstruct
    fun init() {
        try {
            Files.createDirectories(tempDir)
            Files.createDirectories(downloadDir)

        } catch (e: IOException) {
            throw StorageException("Не удалось инициализировать хранилище", e)
        }
    }

    @Transactional
    fun storeTemp(file: MultipartFile): String {

        logger.debug("Начинаем сохранение файла: ${file.originalFilename.toString()}")
        val uploadFileData = UploadFileData()
        // Очищаем имя файла от лишних символов
        uploadFileData.originalName = file.originalFilename.toString().replace(Regex("""[\h\s\v!@#$%^&()+\-;,:?*\\/'"]"""), "")

        val filename: String = StringUtils.cleanPath(
                uploadFileData.uuid
                        + EXTENSION_SEPARATOR
                        + StringUtils.getFilenameExtension(file.originalFilename)
        )

        try {
            if (file.isEmpty) {
                throw StorageException("Не удалось сохранить пустой файл")
            }

            /*if (filename.contains("..")) {
                throw StorageException("Не удается сохранить файл с относительным путем вне текущего каталога ${file.originalFilename}")
            }*/

            try {
                val inputStream: InputStream = file.inputStream

                Files.copy(
                        inputStream,
                        this.tempDir.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING
                )

                inputStream.close()

                uploadFileData.path = this.tempDir.resolve(filename).toString()

                val uploadFileDataSave = uploadFileDataRepository.save(uploadFileData)

                logger.debug("<UPLOAD> Сохранили файл: $uploadFileDataSave")

                val uploadFileStatus = UploadFileStatus()
                uploadFileStatus.uuid = uploadFileData.uuid
                uploadFileStatus.path = uploadFileData.path
                val uploadFileStatusSave = uploadFileStatusRepository.save(uploadFileStatus)
                logger.debug("<STATUS> Сохранили $uploadFileStatusSave")

            } catch (e: Exception) {
                logger.debug("Проблема при сохранении файла в директорию ${this.tempDir} и сохранении пути в базу", e)
            }

        } catch (e: IOException) {
            throw StorageException("Не удалось сохранить файл $filename и начальным именем ${file.originalFilename}", e)
        }

        return uploadFileData.uuid
    }

    fun loadAllTemp(): Stream<Path> {
        try {
            return Files.walk(this.tempDir, 1)
                    .filter({path -> !path.equals(this.tempDir)})
                    .map(this.tempDir::relativize)
        } catch (e: IOException) {
            throw StorageException("Не удалось прочитать сохраненные файлы", e)
        }
    }

    fun changeStatus(uuid: String, newStatus: String, newPath: String? = null) {
        val uploadFileStatus: UploadFileStatus? = uploadFileStatusRepository.findByUuid(uuid)
        if (uploadFileStatus != null) {
            uploadFileStatus.status = newStatus
            if (newPath != null) {
                uploadFileStatus.path = newPath
            }
            val uploadFileStatusSave = uploadFileStatusRepository.save(uploadFileStatus)
            logger.debug("Сменили статус для $uuid на ${uploadFileStatusSave.status}")
        } else {
            throw StorageException("Не удалось сменить статус для файла: ${uploadFileStatus?.path}")
        }
    }

    fun loadTemp(uuid: String): Path {
        val uploadFileData: UploadFileData? = uploadFileDataRepository.findByUuid(uuid)
        if (uploadFileData != null) {
            return Paths.get(uploadFileData.path)
        } else {
            throw FileNotFoundException("Не удалось найти файл $uuid")
        }
    }

    fun loadAsResourceTemp(uuid: String): Resource {
        try {
            val file: Path = loadTemp(uuid)
            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                return resource
            } else {
                throw FileNotFoundException("Не удалось прочитать файл $uuid")
            }
        } catch (e: MalformedURLException) {
            throw FileNotFoundException("Не удалось прочитать файл $uuid", e)
        }
    }

    fun deleteFileTemp(uuid: String) {
        val uploadFileData: UploadFileData? = uploadFileDataRepository.findByUuid(uuid)
        logger.debug("<DELETE> Файл для удаления: $uploadFileData")
        if (uploadFileData != null) {
            uploadFileDataRepository.delete(uploadFileData)
            val delFSUtils = FileSystemUtils.deleteRecursively(Paths.get(uploadFileData.path))
            //val delFiles = Files.deleteIfExists(Paths.get(uploadFileData.path))
            logger.debug("Удаление ${uploadFileData.path}: $delFSUtils")
        } else {
            throw StorageException("Не удалось удалить файл с uuid: $uuid")
        }
    }

    fun deleteAllTemp() {
        FileSystemUtils.deleteRecursively(tempDir.toFile())
        uploadFileDataRepository.deleteAll()
    }

    fun createDirectoryUUIDDownload(uuid: String): Path {

        val dir: Path

        try {
            dir = Files.createDirectories(downloadDir.resolve(uuid))
        } catch (e: IOException) {
            throw StorageException("Не удалось создать директорию $uuid", e)
        }

        logger.debug("Создана директория $dir")
        return dir
    }

    @Transactional
    fun storeDownload(downloadFileData: DownloadFileData) {
        logger.debug("<DOWNLOAD> Проверка существования файлов {'${downloadFileData.pathVideoFile}';'${downloadFileData.pathImageFile}'} и сохранение их в базу")
        if (Paths.get(downloadFileData.pathVideoFile).toFile().exists()
                && Paths.get(downloadFileData.pathImageFile).toFile().exists()) {
            val downloadFileDataSave = downloadFileDataRepository.save(downloadFileData)
            logger.debug("<DOWNLOAD> Сохранили в базе данные директории: $downloadFileDataSave")
        } else {
            throw StorageException("Не удалось сохранить в базе данные в директории, возможно директория или файлы в ней не существуют")
        }
    }

    fun loadDownloadPath(uuid: String): List<Path> {
        val downloadFileData: DownloadFileData? = downloadFileDataRepository.findByUuid(uuid)
        if (downloadFileData != null) {
            return listOf(Paths.get(downloadFileData.pathVideoFile), Paths.get(downloadFileData.pathImageFile))
        } else {
            throw FileNotFoundException("Не удалось найти директорию $uuid")
        }
    }

    fun loadAsResourceDownload(uuid: String): List<Resource> {
        try {
            val file: List<Path> = loadDownloadPath(uuid)
            val resourceVideo: Resource = UrlResource(file[0].toUri())
            val resourceThumbnail: Resource = UrlResource(file[1].toUri())
            if ((resourceVideo.exists() || resourceVideo.isReadable) && (resourceThumbnail.exists() || resourceThumbnail.isReadable)) {
                return listOf(resourceVideo, resourceThumbnail)
            } else {
                throw FileNotFoundException("Не удалось прочитать файлы в директории $uuid")
            }
        } catch (e: MalformedURLException) {
            throw StorageException("Не удалось прочитать файлы в директории $uuid", e)
        }
    }

    fun deleteFileDownload(uuid: String) {
        val downloadFileData: DownloadFileData? = downloadFileDataRepository.findByUuid(uuid)
        logger.debug("<DELETE> Файл для удаления: $downloadFileData")
        if (downloadFileData != null) {
            downloadFileDataRepository.delete(downloadFileData)
            val delFSUtils = FileSystemUtils.deleteRecursively(Paths.get(downloadFileData.directoryName))
            //val delFiles = Files.deleteIfExists(Paths.get(uploadFileData.path))
            logger.debug("Удаление ${downloadFileData.directoryName}: $delFSUtils")
        } else {
            throw StorageException("Не удалось удалить файл с uuid: $uuid")
        }
    }

}