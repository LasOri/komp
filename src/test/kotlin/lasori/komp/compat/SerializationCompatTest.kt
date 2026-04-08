package lasori.komp.compat

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import lasori.komp.data.CodingKey
import lasori.komp.data.extension.extractPath
import lasori.komp.data.extension.extractType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
class SerializationCompatTest {

    @Serializable
    data class Flat(val name: String, val age: Int)

    @Serializable
    data class Nested(val inner: Flat, val label: String)

    @Serializable
    data class WithList(val items: List<String>)

    @Serializable
    data class WithEnum(val status: Status)

    @Serializable
    enum class Status { active, inactive }

    private val json = Json { prettyPrint = false }

    @Test
    fun `MissingFieldException contains missingFields for flat class`() {
        val exception = try {
            json.decodeFromString<Flat>("{}")
            null
        } catch (e: MissingFieldException) {
            e
        }

        assertNotNull(exception, "Expected MissingFieldException for empty JSON")
        assertTrue(exception.missingFields.contains("name"), "Expected 'name' in missingFields but got: ${exception.missingFields}")
    }

    @Test
    fun `extractPath works on actual MissingFieldException`() {
        val exception = try {
            json.decodeFromString<Flat>("{}")
            null
        } catch (e: MissingFieldException) {
            e
        }

        assertNotNull(exception)
        val path = exception.extractPath()
        assertNotNull(path, "extractPath should return non-null for MissingFieldException")
    }

    @Test
    fun `extractType works on type mismatch for numeric field`() {
        val exception = try {
            json.decodeFromString<Flat>("""{"name":"test","age":"notAnInt"}""")
            null
        } catch (e: Exception) {
            e
        }

        assertNotNull(exception)
        val type = exception.extractType()
        assertNotNull(type, "extractType should detect numeric type from: ${exception.message}")
    }

    @Test
    fun `extractPath works on nested type mismatch`() {
        val exception = try {
            json.decodeFromString<Nested>("""{"inner":{"name":"ok","age":"bad"},"label":"test"}""")
            null
        } catch (e: Exception) {
            e
        }

        assertNotNull(exception)
        val path = exception.extractPath()
        assertNotNull(path, "extractPath should return path for nested error: ${exception.message}")
    }

    @Test
    fun `extractType detects string type from enum error`() {
        val exception = try {
            json.decodeFromString<WithEnum>("""{"status":null}""")
            null
        } catch (e: Exception) {
            e
        }

        assertNotNull(exception)
        val type = exception.extractType()
        assertNotNull(type, "extractType should detect type from enum error: ${exception.message}")
    }

    @Test
    fun `extractType detects array from bracket error`() {
        val exception = try {
            json.decodeFromString<WithList>("""{"items":"notAnArray"}""")
            null
        } catch (e: Exception) {
            e
        }

        assertNotNull(exception)
        val type = exception.extractType()
        assertEquals("array", type, "Expected 'array' type but got '$type' from: ${exception.message}")
    }

    @Test
    fun `MissingFieldException message format regression`() {
        val exception = try {
            json.decodeFromString<Flat>("{}")
            null
        } catch (e: MissingFieldException) {
            e
        }

        assertNotNull(exception)
        val message = exception.message!!
        assertTrue(
            message.contains("Field") || message.contains("field") || message.contains("missing"),
            "MissingFieldException message format changed: $message"
        )
    }

    @Test
    fun `full kompify cycle works with current serialization version`() {
        val kompifier = lasori.komp.Kompifier(
            json,
            lasori.komp.data.factory.JsonElementFactory(
                boolGenerator = lasori.komp.data.generator.GenericGenerator(random = lasori.komp.data.generator.random.BoolRandom(42L), seed = 42L),
                intGenerator = lasori.komp.data.generator.GenericGenerator(random = lasori.komp.data.generator.random.IntRandom(42L), seed = 42L),
                doubleGenerator = lasori.komp.data.generator.GenericGenerator(random = lasori.komp.data.generator.random.DoubleRandom(42L), seed = 42L),
                stringGenerator = lasori.komp.data.generator.GenericGenerator(random = lasori.komp.data.generator.random.StringRandom(42L), seed = 42L),
                customGenerators = emptyList(),
                json = json
            )
        )

        val result = kompifier.kompify(null, emptyList()) { j, data ->
            val jsonString = j.encodeToString(kotlinx.serialization.json.JsonElement.serializer(), data)
            j.decodeFromString<Flat>(jsonString) as Any
        } as Flat

        assertNotNull(result.name)
        assertNotNull(result.age)
    }
}
