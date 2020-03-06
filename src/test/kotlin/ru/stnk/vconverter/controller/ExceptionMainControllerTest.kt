package ru.stnk.vconverter.controller

import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
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
class ExceptionMainControllerTest(
        @Autowired val mockMvc: MockMvc
) {

    val description: List<FieldDescriptor> = listOf(
            PayloadDocumentation.fieldWithPath("error")
                    .description("Содержит код ошибки").optional(),
            PayloadDocumentation.subsectionWithPath("data")
                    .description("Содержит данные ответа")
    )

    @Test
    @Throws(Exception::class)
    fun exceptionFileExtension() {
        val multipartFile: MockMultipartFile = MockMultipartFile(
                "file",
                "внутри лапенко 2 серия.gif",
                "multipart/form-data",
                Files.newInputStream(Paths.get("./src/test/resources/внутри лапенко 2 серия.mp4"))
        )

        this.mockMvc.perform(
                MockMvcRequestBuilders.multipart("/upload").file(multipartFile)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.`is`(150)))

    }
}