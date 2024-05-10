package lasori.komp.data.extension

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import lasori.komp.data.CodingKey
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class JsonElementKtTest {

    private val json = Json {
        prettyPrint = false
    }

    private val jsonElement: JsonElement = json.parseToJsonElement(
        """{"key":"value","key2":{"subKey":"subValue"}}"""
    )

    @Test
    fun testGet_shouldReturn_subValue() {
        val result = jsonElement[listOf(CodingKey("key2"), CodingKey("subKey"))]

        assertEquals(JsonPrimitive("subValue"), result)
    }

    @Test
    fun testUpdate_shouldUpdate_subKey() {
        val expected = """{"key":"value","key2":{"subKey":"subValue2"}}"""

        val codingPath = listOf(CodingKey("key2"), CodingKey("subKey"))

        val updated = jsonElement.update(codingPath, JsonPrimitive("subValue2"))

        val result = json.encodeToString(updated)

        assertEquals(expected, result)
    }
}
