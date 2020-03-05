package ru.stnk.vconverter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.stnk.vconverter.entity.UploadFileData

@Repository
interface UploadFileDataRepository: JpaRepository<UploadFileData, Long> {
    fun findByUuid(uuid: String): UploadFileData?
}