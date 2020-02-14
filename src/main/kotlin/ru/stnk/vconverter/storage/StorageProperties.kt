package ru.stnk.vconverter.storage

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "storage")
class StorageProperties (val location: String) {

    /*private lateinit var location: String

    fun getLocation(): String {
        return location
    }

    fun setLocation(location: String) {
        this.location = location
    }*/
}