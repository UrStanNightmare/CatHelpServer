package ru.urstannightmare.cathelpserver

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

const val requestDate = "2023-08-22"

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(OrderAnnotation::class)
class CatHelpServerApplicationTests{

    @Autowired
    private val mockMvc: MockMvc? = null

    @Test
    @Order(1)
    @WithMockUser("user")
    fun test_alive_endpoint() {
        val requestResult = mockMvc!!.perform(
            get("$apiV2Path/health")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andReturn()

        val responseJsonObject = JSONObject(requestResult.response.contentAsString)

        assertThat(
            isJsonHasNeededKeyValues(
                responseJsonObject,
                mapOf("status" to "Alive")
            )
        )
            .`as`("Check health endpoint")
            .isTrue()
    }

    @Test
    @Order(2)
    fun test_get_tasks_by_date_when_db_is_empty() {
        val tasksKey = "tasks"
        val generatedDateKey = "generatedDate"

        val requestResult = mockMvc!!.perform(
            get("$apiV2Path/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .param("date", requestDate)
        ).andExpect(status().isOk)
            .andReturn()

        val responseJsonObject = JSONObject(requestResult.response.contentAsString)

        assertThat(
            isJsonHasSpecifiedKeys(
                responseJsonObject,
                arrayOf(generatedDateKey, tasksKey)
            )
        )
            .`as`("Must have default keys")
            .isTrue()

        val tasks = responseJsonObject.get(tasksKey) as JSONArray

        assertThat(tasks.length() == 0)
            .`as`("Must be empty")
            .isTrue()
    }

    @Test
    @Order(3)
    fun test_get_tasks_when_data_is_not_specified() {
        val requestResult = mockMvc!!.perform(
            get("$apiV2Path/tasks")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
            .andReturn()
        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isErrorResponseContainsAllBasicFields(jsonResult))
            .`as`("Basic field existence check for error response")
            .isTrue()

        assertThat(
            isJsonHasNeededKeyValues(
                jsonResult,
                mapOf(
                    "code" to 400
                )
            )
        ).`as`("Error response must have expected values")
    }

    @Test
    @Order(4)
    fun test_post_task_when_request_value_incorrect() {
        val requestResult = mockMvc!!.perform(
            post("$apiV2Path/task")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
            .andReturn()
        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isErrorResponseContainsAllBasicFields(jsonResult))
            .`as`("Basic field existence check for error response")
            .isTrue()

        assertThat(
            isJsonHasNeededKeyValues(
                jsonResult,
                mapOf(
                    "code" to 400,
                    "details" to "Incorrect request value [requestBody]"
                )
            )
        ).`as`("Error response must have expected values")
    }

    @Test
    @Order(5)
    fun test_post_task_when_request_correct() {
        val description = "Test desc"
        val isDone = "true"
        val expectingDate = "${requestDate}T00:00:00.000+00:00"

        val requestContentBody =
            """
            {
                "date": "$requestDate",
                "description": "$description",
                "isDone": "$isDone"
            } 
            """.trimIndent()

        val requestResult = mockMvc!!.perform(
            post("$apiV2Path/task")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContentBody)
        ).andExpect(status().isOk)
            .andReturn()
        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isJsonHasSpecifiedKeys(jsonResult, arrayOf("generatedDate", "tasks")))
            .`as`("Check basic fields existence for response")
            .isTrue()

        var tasksJson = jsonResult.getJSONArray("tasks");
        assertThat(tasksJson.length())
            .`as`("Json array length must be == 1 cause added only one task")
            .isEqualTo(1)

        var taskJson = tasksJson.get(0) as JSONObject
        assertThat(isJsonHasSpecifiedKeys(taskJson, arrayOf("id", "date", "description", "isDone")))
            .`as`("Check basic fields existence")
            .isTrue()

        assertThat(
            isJsonHasNeededKeyValues(
                taskJson,
                mapOf(
                    "id" to "1",
                    "date" to expectingDate,
                    "description" to description,
                    "isDone" to isDone,
                )
            )
        )
            .`as`("Check some field values")
            .isTrue()
    }

    @Test
    @Order(6)
    fun test_post_change_task_status_with_incorrect_request_data(){
        val requestResult = mockMvc!!.perform(
            post("$apiV2Path/task/-1/status")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
            .andReturn()

        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isErrorResponseContainsAllBasicFields(jsonResult))
            .`as`("Must be error response")
            .isTrue()


        assertThat(isJsonHasNeededKeyValues(jsonResult, mapOf("code" to "400")))
            .`as`("Must have successful status")
            .isTrue()
    }

    @Test
    @Order(7)
    fun test_post_change_task_status_with_wrong_task_id(){
        val wrongId = 100

        val expectedMessage = "Can't find task with id $wrongId"

        val requestResult = mockMvc!!.perform(
            post("$apiV2Path/task/$wrongId/status")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
            .andReturn()

        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isErrorResponseContainsAllBasicFields(jsonResult))
            .`as`("Must be error response")
            .isTrue()


        assertThat(isJsonHasNeededKeyValues(jsonResult, mapOf(
            "code" to "404",
            "details" to expectedMessage
        )))
            .`as`("Must have successful status")
            .isTrue()
    }

    @Test
    @Order(8)
    fun test_post_change_task_status_with_correct_request_data(){
        val requestResult = mockMvc!!.perform(
            post("$apiV2Path/task/1/status")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andReturn()

        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isJsonHasSpecifiedKeys(jsonResult, arrayOf("status")))
            .`as`("Must be status response")
            .isTrue()
    }


    @Test
    @Order(9)
    fun test_delete_task_when_request_correct_and_user_in_db() {
        val expectedString = "Deleted"

        val requestResult = mockMvc!!.perform(
            delete("$apiV2Path/task/1/")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andReturn()

        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isJsonHasSpecifiedKeys(jsonResult, arrayOf("status")))
            .`as`("Must have status field")
            .isTrue()

        assertThat(isJsonHasNeededKeyValues(jsonResult, mapOf("status" to expectedString)))
            .`as`("Must have successful status")
            .isTrue()
    }

    @Test
    @Order(10)
    fun test_delete_task_when_request_correct_and_db_empty() {
        val expectedString = "Nothing"

        val requestResult = mockMvc!!.perform(
            delete("$apiV2Path/task/1/")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andReturn()

        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isJsonHasSpecifiedKeys(jsonResult, arrayOf("status")))
            .`as`("Must have status field")
            .isTrue()

        assertThat(isJsonHasNeededKeyValues(jsonResult, mapOf("status" to expectedString)))
            .`as`("Must have successful status")
            .isTrue()
    }

    @Test
    @Order(11)
    fun test_delete_task_when_request_has_incorrect_data() {
        val requestResult = mockMvc!!.perform(
            delete("$apiV2Path/task/-1/")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
            .andReturn()

        val jsonResult = JSONObject(requestResult.response.contentAsString)

        assertThat(isErrorResponseContainsAllBasicFields(jsonResult))
            .`as`("Must be error response")
            .isTrue()


        assertThat(isJsonHasNeededKeyValues(jsonResult, mapOf("code" to "400")))
            .`as`("Must have successful status")
            .isTrue()
    }


    fun isJsonHasSpecifiedKeys(json: JSONObject, keys: Array<String>): Boolean {
        for (key in keys) {
            if (!json.has(key)) {
                return false
            }
        }

        return true;
    }

    fun isJsonFieldEqualsSpecified(json: JSONObject, field: String, expected: Any): Boolean {
        val value = json.get(field).toString()

        if (value != expected) {
            println("Field $field has value $value instead of $expected")
            return false
        }
        return true
    }

    fun isJsonHasNeededKeyValues(json: JSONObject, expected: Map<String, Any>): Boolean {
        for (entry in expected.entries) {
            if (!isJsonFieldEqualsSpecified(json = json, field = entry.key, expected = entry.value)) {
                return false
            }
        }
        return true
    }

    fun isErrorResponseContainsAllBasicFields(errorJson: JSONObject): Boolean {
        return isJsonHasSpecifiedKeys(errorJson, arrayOf("code", "details", "path"))
    }
}
