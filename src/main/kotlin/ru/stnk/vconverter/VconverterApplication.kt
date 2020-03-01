package ru.stnk.vconverter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.stnk.vconverter.storage.FileSystemStorageService
import ru.stnk.vconverter.storage.StorageProperties
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
@EnableJpaAuditing
@EnableAsync
class VconverterApplication {

	@Bean
	fun init(fileSystemStorageService: FileSystemStorageService): CommandLineRunner {
		return CommandLineRunner {
			fileSystemStorageService.deleteAllTemp()
			fileSystemStorageService.init()
		}
	}

}

fun main(args: Array<String>) {
	runApplication<VconverterApplication>(*args)

	// https://mkyong.com/java/java-processbuilder-examples/
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

	// https://www.baeldung.com/run-shell-command-in-java

	/*
	val isWindows: Boolean = System.getProperty("os.name").toLowerCase().startsWith("windows")
	val builder = ProcessBuilder()
	if (isWindows) {
		builder.command("cmd.exe", "/c", "dir")
	} else {
		builder.command("sh", "-c", "ls")
	}
	builder.directory(File(System.getProperty("user.home")))
	val process = builder.start()
	val streamGobbler = StreamGobbler(process.inputStream, Consumer {x: String? -> println(x)})
	//Executors.newSingleThreadExecutor().submit(streamGobbler)
	ThreadPoolTaskExecutor().submit(streamGobbler)
	val exitCode = process.waitFor()
	assert(exitCode == 0)
	*/

}

/*private class StreamGobbler(
		private val inputStream: InputStream,
		private val consumer: Consumer<String>
) : Runnable {
	override fun run() {
		BufferedReader(InputStreamReader(inputStream)).lines()
				.forEach(consumer)
	}

}*/
