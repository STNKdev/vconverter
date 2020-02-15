package ru.stnk.vconverter.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID
import javax.persistence.*


@Entity
class UploadFileData: AuditModel() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    var id: Long? = null

    @Column(name = "uuid")
    val uuid: UUID = UUID.randomUUID()

    @Column(name = "original_name")
    lateinit var original_name: String
}