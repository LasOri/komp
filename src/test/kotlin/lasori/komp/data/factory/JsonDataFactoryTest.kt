package lasori.komp.data.factory

import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import lasori.komp.data.generator.Generator
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class JsonDataFactoryTest {

    private val json: Json = Json

    private lateinit var mockBoolGenerator: Generator<Boolean>
    private lateinit var mockIntGenerator: Generator<Int>
    private lateinit var mockDoubleGenerator: Generator<Double>
    private lateinit var mockStringGenerator: Generator<String>

    private lateinit var factory: Factory<JsonDataType, JsonElement>

    @BeforeTest
    fun setUp() {
        mockBoolGenerator = mockk(relaxed = true)
        mockIntGenerator = mockk(relaxed = true)
        mockDoubleGenerator = mockk(relaxed = true)
        mockStringGenerator = mockk(relaxed = true)
        factory = JsonDataFactory(mockBoolGenerator, mockIntGenerator, mockDoubleGenerator, mockStringGenerator)
    }

    @Test
    fun testCreate_shouldReturn_true() {
        every { mockBoolGenerator.generate() } returns true

        val result = factory.create(JsonDataType.boolean)

        assertTrue { json.decodeFromJsonElement(result) }
    }

    @Test
    fun testCreate_shouldReturn_42() {
        every { mockIntGenerator.generate() } returns 42

        val result = factory.create(JsonDataType.int)

        assertEquals(42, json.decodeFromJsonElement(result))
    }

    @Test
    fun testCreate_shouldReturn_3_14() {
        every { mockDoubleGenerator.generate() } returns 3.14

        val result = factory.create(JsonDataType.double)

        assertEquals(3.14, json.decodeFromJsonElement(result))
    }

    @Test
    fun testCreate_shouldReturn_text() {
        every { mockStringGenerator.generate() } returns "text"

        val result = factory.create(JsonDataType.string)

        assertEquals("text", json.decodeFromJsonElement(result))
    }

    @Test
    fun testCreate_shouldReturn_array() {
        val result = factory.create(JsonDataType.array)

        assertEquals(emptyList(), json.decodeFromJsonElement<List<String>>(result))
    }

    @Test
    fun testCreate_shouldReturn_map() {
        val result = factory.create(JsonDataType.`object`)

        assertEquals(emptyMap(), json.decodeFromJsonElement<Map<String, String>>(result))
    }

    @Test
    fun testCreate_shouldReturn_null() {
        val result = factory.create(JsonDataType.`null`)

        assertEquals(JsonNull, result)
    }

}
