package ru.stnk.vconverter.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.Resource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import ru.stnk.vconverter.storage.FileSystemStorageService


@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class MainControllerDownloadTest (
        @Autowired val mockMvc: MockMvc
) {
    @MockBean
    lateinit var fileSystemStorageService: FileSystemStorageService

    @Test
    @Throws(Exception::class)
    fun downloadFile() {

    }
}