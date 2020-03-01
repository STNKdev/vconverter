package ru.stnk.vconverter.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.stnk.vconverter.entity.DownloadFileData

interface DownloadFileDataRepository: JpaRepository<DownloadFileData, Long> {
    fun findByUuid(uuid: String): DownloadFileData?
}