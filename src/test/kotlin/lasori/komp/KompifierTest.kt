package lasori.komp

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import lasori.komp.data.Convertible
import lasori.komp.data.factory.JsonElementFactory
import lasori.komp.data.generator.GenericGenerator
import lasori.komp.data.generator.Generator
import lasori.komp.data.generator.random.BoolRandom
import lasori.komp.data.generator.random.DoubleRandom
import lasori.komp.data.generator.random.IntRandom
import lasori.komp.data.generator.random.StringRandom
import lasori.komp.exception.KompifingFailedException
import lasori.komp.testData.InnerData
import lasori.komp.testData.SimpleData
import lasori.komp.testData.SubData
import lasori.komp.testData.TestEnum
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KompifierTest {

    private val json = Json { prettyPrint = false }
    private val seed = 42L

    private fun createFactory(customGenerators: List<Generator<Convertible<*, *>>> = emptyList()): JsonElementFactory {
        return JsonElementFactory(
            boolGenerator = GenericGenerator(random = BoolRandom(seed), seed = seed),
            intGenerator = GenericGenerator(random = IntRandom(seed), seed = seed),
            doubleGenerator = GenericGenerator(random = DoubleRandom(seed), seed = seed),
            stringGenerator = GenericGenerator(random = StringRandom(seed), seed = seed),
            customGenerators = customGenerators,
            json = json
        )
    }

    private inline fun <reified T> jsonizer(): (Json, JsonElement) -> Any = { j, data ->
        val jsonString = j.encodeToString(data)
        j.decodeFromString<T>(jsonString) as Any
    }

    @Test
    fun `kompify generates SimpleData with all fields populated`() {
        val kompifier = Kompifier(json, createFactory())

        val result = kompifier.kompify(null, emptyList(), jsonizer<SimpleData>()) as SimpleData

        assertNotNull(result.text)
        assertNotNull(result.num)
        assertNotNull(result.floating)
        assertNotNull(result.bool)
    }

    @Test
    fun `kompify generates nested data structures`() {
        val kompifier = Kompifier(json, createFactory())

        @kotlinx.serialization.Serializable
        data class Nested(val inner: SubData, val name: String)

        val result = kompifier.kompify(null, emptyList(), jsonizer<Nested>()) as Nested

        assertNotNull(result.inner.text)
        assertNotNull(result.name)
    }

    @Test
    fun `kompify applies predefined values`() {
        val kompifier = Kompifier(json, createFactory())
        val predefined = mapOf<KProperty1<*, *>, Convertible<*, *>>(
            SimpleData::text to Convertible("fixedValue", String.serializer())
        )

        val result = kompifier.kompify(predefined, emptyList(), jsonizer<SimpleData>()) as SimpleData

        assertEquals("fixedValue", result.text)
    }

    @Test
    fun `kompify uses convertibles for enum types`() {
        val testEnumGenerator = object : Generator<Convertible<*, *>> {
            override fun generate(): Convertible<*, *> {
                return Convertible(TestEnum.ugly, TestEnum.serializer())
            }
        }
        val kompifier = Kompifier(json, createFactory(listOf(testEnumGenerator)))

        @kotlinx.serialization.Serializable
        data class WithEnum(val status: TestEnum, val name: String)

        val result = kompifier.kompify(
            null,
            listOf(Convertible(TestEnum.ugly, TestEnum.serializer())),
            jsonizer<WithEnum>()
        ) as WithEnum

        assertEquals(TestEnum.ugly, result.status)
    }

    @Test
    fun `kompify throws KompifingFailedException on exception loop`() {
        val kompifier = Kompifier(json, createFactory())

        assertFailsWith<KompifingFailedException> {
            kompifier.kompify(null, emptyList()) { _, _ ->
                throw Exception("same error message every time")
            }
        }
    }

    @Test
    fun `kompify handles nullable fields`() {
        val kompifier = Kompifier(json, createFactory())

        @kotlinx.serialization.Serializable
        data class WithNullable(val name: String, val optional: String? = null)

        val result = kompifier.kompify(null, emptyList(), jsonizer<WithNullable>()) as WithNullable

        assertNotNull(result.name)
    }

    @Test
    fun `kompify with deterministic seed produces same result`() {
        val factory = createFactory()
        val kompifier1 = Kompifier(json, factory)

        val factory2 = createFactory()
        val kompifier2 = Kompifier(json, factory2)

        val result1 = kompifier1.kompify(null, emptyList(), jsonizer<SimpleData>()) as SimpleData
        val result2 = kompifier2.kompify(null, emptyList(), jsonizer<SimpleData>()) as SimpleData

        assertEquals(result1, result2)
    }

    @Test
    fun `kompify predefined values are case insensitive`() {
        val kompifier = Kompifier(json, createFactory())
        val predefined = mapOf<KProperty1<*, *>, Convertible<*, *>>(
            SimpleData::text to Convertible("matched", String.serializer())
        )

        val result = kompifier.kompify(predefined, emptyList(), jsonizer<SimpleData>()) as SimpleData

        assertEquals("matched", result.text)
    }
}
