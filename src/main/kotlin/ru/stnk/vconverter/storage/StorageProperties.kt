package ru.stnk.vconverter.storage

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "storage.location")
class StorageProperties /*(val location: String)*/ {

    lateinit var temp: String

    lateinit var download: String
}