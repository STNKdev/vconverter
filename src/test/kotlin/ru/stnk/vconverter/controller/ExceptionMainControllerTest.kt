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
class ExceptionMainControllerTest(
        @Autowired val mockMvc: MockMvc
) {

    /*
    * 500 - Внутренняя ошибка сервиса
    * 404 - Неправильная точка вызова
    * 400 - Неправильный запрос
    * 150 - Недопустимый формат файла
    * 151 - Нет файла в запросе
    * 152 - Неверный идентификатор
    * 153 - Файл не найден
    * 154 - Размер файла слишком велик (undertow закрывает соединение, кода ошибки нет)
    *
    * */

    val description: List<FieldDescriptor> = listOf(
            PayloadDocumentation.fieldWithPath("error")
                    .description("Содержит код ошибки"),
            PayloadDocumentation.subsectionWithPath("data")
                    .description("Содержит данные ответа"),
            PayloadDocumentation.fieldWithPath("data['description']")
                    .description("Содержит описание ошибки")
    )

    @Test
    @Throws(Exception::class)
    fun exceptionFileExtension() {
        val multipartFile = MockMultipartFile(
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.`is`(150)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))
    }

    @Test
    @Throws(Exception::class)
    fun exceptionMultipartFileEmpty() {
        val multipartFile = MockMultipartFile(
                "file",
                byteArrayOf()
        )

        this.mockMvc.perform(
                MockMvcRequestBuilders.multipart("/upload").file(multipartFile)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.`is`(151)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))
    }

    @Test
    @Throws(Exception::class)
    fun exceptionFileNotFoundException() {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/download/8651eb16-dfd4-4a3b-ab00-0f2a356289ab.mp4")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.`is`(153)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))
    }

    @Test
    @Throws(Exception::class)
    fun exceptionUuidNotFoundException() {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/check")
                        .param("id", "12345-65478")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.`is`(152)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))
    }

    @Test
    @Throws(Exception::class)
    fun exceptionBadRequestException() {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/check")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.`is`(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))
    }

    @Test
    @Throws(Exception::class)
    fun exceptionNotFoundEndpointException() {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/sdgtfxgkv")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.`is`(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))
    }

}