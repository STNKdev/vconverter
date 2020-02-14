package ru.stnk.vconverter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.stnk.vconverter.storage.StorageProperties


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
class VconverterApplication

fun main(args: Array<String>) {
	runApplication<VconverterApplication>(*args)

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
