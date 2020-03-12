package ru.stnk.vconverter.controller

import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.nio.file.Files
import java.nio.file.Paths


@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class MainControllerUploadTest (
        @Autowired val mockMvc: MockMvc
) {

    val description: List<FieldDescriptor> = listOf(
            PayloadDocumentation.subsectionWithPath("data")
                    .description("Содержит данные ответа")
    )

    @Test
    @Throws(Exception::class)
    fun uploadFile() {
        val multipartFile = MockMultipartFile(
                "file",
                "внутри лапенко 2 серия.mp4",
                "multipart/form-data",
                Files.newInputStream(Paths.get("./src/test/resources/внутри лапенко 2 серия.mp4"))
        )

        this.mockMvc.perform(
                MockMvcRequestBuilders.multipart("/upload").file(multipartFile)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                                .and(
                                        PayloadDocumentation.fieldWithPath("data['id']")
                                                .description("Содержит id для отслеживания видеофайла на обработке")
                                )
                ))

        //verify(fileSystemStorageService, times(1)).storeTemp(multipartFile)

    }

}