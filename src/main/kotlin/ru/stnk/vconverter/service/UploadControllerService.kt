package ru.stnk.vconverter.service

import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import ru.stnk.vconverter.storage.FileSystemStorageService
import ru.stnk.vconverter.storage.exception.FileExtentionException


@Service
class UploadControllerService (
        private val storageService: FileSystemStorageService
) {
    fun checkAndSaveFile (file: MultipartFile): String {
        when (StringUtils.getFilenameExtension(file.originalFilename)) {
            "mov","avi","wmv","flv","3gp","mp4","mpg" -> return storageService.storeTemp(file)
            else -> throw FileExtentionException("Недопустимый формат файла")
        }
    }
}