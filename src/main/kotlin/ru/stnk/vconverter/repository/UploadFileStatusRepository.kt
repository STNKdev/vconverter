package ru.stnk.vconverter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.stnk.vconverter.entity.UploadFileStatus

@Repository
interface UploadFileStatusRepository: JpaRepository<UploadFileStatus, Long> {
    fun findByUuid(uuid: String): UploadFileStatus?
}