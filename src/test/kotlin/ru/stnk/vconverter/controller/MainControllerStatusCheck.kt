package ru.stnk.vconverter.controller

import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.stnk.vconverter.service.MainControllerService


@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class MainControllerStatusCheck (
        @Autowired val mockMvc: MockMvc
) {
    @MockBean
    lateinit var mainControllerService: MainControllerService

    val description: List<FieldDescriptor> = listOf(
            PayloadDocumentation.fieldWithPath("data['status']")
                    .description("Статус обработки видеофайла")
    )

    // 8651eb16-dfd4-4a3b-ab00-0f2a356289ab
    val idString = "8651eb16-dfd4-4a3b-ab00-0f2a356289ab"

    @Test
    //@Throws(Exception::class)
    fun statusInQueue() {

        val inQueueString = "В очереди"

        Mockito.`when`(mainControllerService.checkStatus(idString)).thenReturn(inQueueString)

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/check")
                        .param("id", idString)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))

        Mockito.verify(mainControllerService, Mockito.times(1)).checkStatus(idString)
    }

    @Test
    fun statusHandles() {

        val handlesString = "Обрабатывается"

        Mockito.`when`(mainControllerService.checkStatus(idString)).thenReturn(handlesString)

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/check")
                        .param("id", idString)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))

        Mockito.verify(mainControllerService, Mockito.times(1)).checkStatus(idString)
    }

    @Test
    fun statusDone() {

        val doneString = "Готово"

        Mockito.`when`(mainControllerService.checkStatus(idString)).thenReturn(doneString)

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/check")
                        .param("id", idString)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", Matchers.isA<String>(String::class.java)))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(description)
                ))

        Mockito.verify(mainControllerService, Mockito.times(1)).checkStatus(idString)
    }

}