package lasori.komp.data.factory

import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import lasori.komp.data.Convertible
import lasori.komp.data.generator.Generator
import lasori.komp.testData.TestEnum
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsonElementFactoryTest {

    private val json: Json = Json

    private lateinit var mockBoolGenerator: Generator<Boolean>
    private lateinit var mockIntGenerator: Generator<Int>
    private lateinit var mockDoubleGenerator: Generator<Double>
    private lateinit var mockStringGenerator: Generator<String>
    private lateinit var mockCustomGenerator: Generator<Convertible<*, *>>

    private lateinit var factory: Factory<String, JsonElement>

    @BeforeTest
    fun setUp() {
        mockBoolGenerator = mockk(relaxed = true)
        mockIntGenerator = mockk(relaxed = true)
        mockDoubleGenerator = mockk(relaxed = true)
        mockStringGenerator = mockk(relaxed = true)
        mockCustomGenerator = mockk(relaxed = true)

        factory = JsonElementFactory(
            mockBoolGenerator,
            mockIntGenerator,
            mockDoubleGenerator,
            mockStringGenerator,
            listOf(mockCustomGenerator),
            json
        )
    }

    @Test
    fun testCreate_shouldReturn_true() {
        every { mockBoolGenerator.generate() } returns true

        val result = factory.create("boolean")

        assertTrue { json.decodeFromJsonElement(result) }
    }

    @Test
    fun testCreate_shouldReturn_42() {
        every { mockIntGenerator.generate() } returns 42

        val result = factory.create("numeric")

        assertEquals(42, json.decodeFromJsonElement(result))
    }

    @Test
    fun testCreate_shouldReturn_3_14() {
        every { mockDoubleGenerator.generate() } returns 3.14

        val result = factory.create("double")

        assertEquals(3.14, json.decodeFromJsonElement(result))
    }

    @Test
    fun testCreate_shouldReturn_text() {
        every { mockStringGenerator.generate() } returns "text"

        val result = factory.create("string")

        assertEquals("text", json.decodeFromJsonElement(result))
    }

    @Test
    fun testCreate_shouldReturn_array() {
        val result = factory.create("array")

        assertEquals(emptyList(), json.decodeFromJsonElement<List<String>>(result))
    }

    @Test
    fun testCreate_shouldReturn_map() {
        val result = factory.create("object")

        assertEquals(emptyMap(), json.decodeFromJsonElement<Map<String, String>>(result))
    }

    @Test
    fun testCreate_shouldReturn_null() {
        val result = factory.create("null")

        assertEquals(JsonNull, result)
    }

    @Test
    fun testCreate_shouldReturnTestEnum() {
        val convertible = Convertible(TestEnum.bad, TestEnum.serializer())
        every { mockCustomGenerator.generate() } returns convertible

        val result = factory.create("TestEnum")

        assertEquals(convertible.toJsonElement(json), result)
    }

}
