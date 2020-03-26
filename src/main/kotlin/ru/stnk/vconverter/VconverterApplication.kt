package ru.stnk.vconverter

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import ru.stnk.vconverter.entity.UploadFileStatus
import ru.stnk.vconverter.repository.UploadFileStatusRepository
import ru.stnk.vconverter.storage.FileSystemStorageService
import ru.stnk.vconverter.storage.StorageProperties
import ru.stnk.vconverter.task.TaskConvertVideo
import java.io.File
import java.nio.file.Path


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
@EnableJpaAuditing
@EnableAsync
class VconverterApplication {

	@Bean
	fun init(fileSystemStorageService: FileSystemStorageService,
			 fileStatusRepository: UploadFileStatusRepository,
			 taskConvertVideo: TaskConvertVideo): CommandLineRunner {
		return CommandLineRunner {
			//fileSystemStorageService.deleteAllTemp()
			//-----
			// Формируем весь список статусов файлов
			val filesTmp: List<UploadFileStatus> = fileStatusRepository.findAll()
			if (filesTmp.isNotEmpty()) {
				for(file in filesTmp) {
					if (file.status.equals("В очереди")
							|| file.status.equals("Обрабатывается")) {
						//
						val path: Path = fileSystemStorageService.loadTemp(file.uuid)
						val fileTmp: File = path.toFile()
						// Проверяем что файл существует и читается
						if (fileTmp.exists() && fileTmp.canRead()) {
							// удаляем директорию с начатым обработанным файлом
							fileSystemStorageService.deleteFileDownload(file.uuid)
							// удаляем запись со статусом, т.к. она создастся заново
							fileStatusRepository.delete(file)
							// запускаем задачу в фоновом режиме (ставим в очередь на выполнение)
							taskConvertVideo.runTask(file.uuid)
						}
					}
				}
			}
			//-----
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
