package ru.stnk.vconverter

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import ru.stnk.vconverter.storage.FileSystemStorageService
import ru.stnk.vconverter.storage.StorageProperties


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
@EnableJpaAuditing
class VconverterApplication

fun main(args: Array<String>) {
	runApplication<VconverterApplication>(*args)

	@Bean
	fun init(fileSystemStorageService: FileSystemStorageService): CommandLineRunner {
		return CommandLineRunner {
			fileSystemStorageService.deleteAllTemp()
			fileSystemStorageService.init()
		}
	}

	/*val processBuilder = ProcessBuilder()

	// Run this on Windows, cmd, /c = terminate after this run
	// Run this on Windows, cmd, /c = terminate after this run
	processBuilder.command("cmd.exe", "/U", "/c", "ping -n 3 google.com")

	try {
		val process = processBuilder.start()
		// blocked :(
		val reader = BufferedReader(InputStreamReader(process.inputStream))
		var line: String?
		while (reader.readLine().also { line = it } != null) {
			println(line)
		}
		val exitCode = process.waitFor()
		println("\nExited with error code : $exitCode")
	} catch (e: IOException) {
		e.printStackTrace()
	} catch (e: InterruptedException) {
		e.printStackTrace()
	}*/

}
