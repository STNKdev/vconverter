package ru.stnk.vconverter.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.stnk.vconverter.storage.FileSystemStorageService
import java.nio.file.Path
import java.nio.file.Paths


@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class MainControllerDownloadTest (
        @Autowired val mockMvc: MockMvc
) {
    @MockBean
    lateinit var fileSystemStorageService: FileSystemStorageService

    //val idString = "8651eb16-dfd4-4a3b-ab00-0f2a356289ab"
    val idString = "555"

    @BeforeEach
    fun init() {
        val file: List<Path> = listOf(
                Paths.get("./src/test/resources/src/555.mp4"),
                Paths.get("./src/test/resources/src/555.jpg")
        )

        val video: Resource = UrlResource(file[0].toUri())
        val image: Resource = UrlResource(file[1].toUri())

        //Mockito.`when`(fileSystemStorageService.loadDownloadPath(anyString())).thenReturn(file)
        Mockito.`when`(fileSystemStorageService.loadAsResourceDownload(anyString())).thenReturn(listOf(video, image))

    }

    @Test
    @Throws(Exception::class)
    fun downloadVideoFile() {

        this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/download/{id-file}", "$idString.mp4")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$idString.mp4"))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("id-file")
                                        .description("{id} обработанного видеофайла с расширением mp4")
                        )
                ))

        //Mockito.verify(fileSystemStorageService, Mockito.times(1)).loadDownloadPath(idString)
        Mockito.verify(fileSystemStorageService, Mockito.times(1)).loadAsResourceDownload(idString)
    }

    @Test
    @Throws(Exception::class)
    fun downloadImageFile() {

        this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/download/{id-file}", "$idString.jpg")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$idString.jpg"))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("id-file")
                                        .description("{id} обработанного видеофайла с расширением jpg")
                        )
                ))

        //Mockito.verify(fileSystemStorageService, Mockito.times(1)).loadDownloadPath(idString)
        Mockito.verify(fileSystemStorageService, Mockito.times(1)).loadAsResourceDownload(idString)
    }

}