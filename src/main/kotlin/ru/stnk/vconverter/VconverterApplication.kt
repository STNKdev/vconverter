package ru.stnk.vconverter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VconverterApplication

fun main(args: Array<String>) {
	runApplication<VconverterApplication>(*args)
}
