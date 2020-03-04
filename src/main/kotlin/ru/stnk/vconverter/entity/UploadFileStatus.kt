package ru.stnk.vconverter.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class UploadFileStatus: AuditModel() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    var id: Long? = null

    @Column(name = "uuid")
    lateinit var uuid: String

    @Column(name = "path")
    lateinit var path: String

    @Column(name = "status")
    var status: String = "В очереди"
        set(value) {
            when(value) {
                "0" -> field = "В очереди"
                "1" -> field = "Обрабатывается"
                "2" -> field = "Готово"
            }
        }

    override fun toString(): String {
        return "id: $id, uuid: $uuid, path: $path, status: $status"
    }
}