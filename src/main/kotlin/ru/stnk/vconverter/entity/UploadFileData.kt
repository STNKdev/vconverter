package ru.stnk.vconverter.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
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
    val uuid: String = UUID.randomUUID().toString()

    @Column(name = "original_name")
    lateinit var originalName: String

    @Column(name = "path")
    lateinit var path: String

    override fun toString(): String {
        return "id: $id, creatData: $createdAt, uuid: $uuid, original_name: $originalName, path: $path"
    }

}