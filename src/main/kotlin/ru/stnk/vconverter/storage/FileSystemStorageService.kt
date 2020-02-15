package ru.stnk.vconverter.storage

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.storage.exception.FileNotFoundException
import ru.stnk.vconverter.storage.exception.StorageException
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.stream.Stream
import javax.annotation.PostConstruct


@Service
class FileSystemStorageService(
        properties: StorageProperties
): StorageService {

    private val rootLocation: Path = Paths.get(properties.getLocation())

    private val EXTENSION_SEPARATOR = '.'

    @PostConstruct
    override fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Не удалось инициализировать хранилище", e)
        }
    }

    override fun store(file: MultipartFile): String {

        val filename: String = StringUtils.cleanPath(
                UUID.randomUUID().toString()
                        + EXTENSION_SEPARATOR
                        + StringUtils.getFilenameExtension(file.originalFilename)
        )

        try {
            if (file.isEmpty) {
                throw StorageException("Не удалось сохранить пустой файл")
            }

            if (filename.contains("..")) {
                throw StorageException("Не удается сохранить файл с относительным путем вне текущего каталога ${file.originalFilename}")
            }

            try {
                val inputStream: InputStream = file.inputStream

                Files.copy(
                        inputStream,
                        this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING
                )

                inputStream.close()

            } catch (e: Exception) {}

        } catch (e: IOException) {
            throw StorageException("Не удалось сохранить файл $filename и начальным именем ${file.originalFilename}", e)
        }

        return filename
    }

    override fun loadAll(): Stream<Path> {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter({path -> !path.equals(this.rootLocation)})
                    .map(this.rootLocation::relativize)
        } catch (e: IOException) {
            throw StorageException("Не удалось прочитать сохраненные файлы", e)
        }
    }

    override fun load(filename: String): Path {
        return rootLocation.resolve(filename)
    }

    override fun loadAsResource(filename: String): Resource {
        try {
            val file: Path = load(filename)
            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                return resource
            } else {
                throw FileNotFoundException("Не удалось прочитать файл $filename")
            }
        } catch (e: MalformedURLException) {
            throw FileNotFoundException("Не удалось прочитать файл $filename", e)
        }
    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }

}