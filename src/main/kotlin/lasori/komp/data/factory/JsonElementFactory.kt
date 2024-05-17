package lasori.komp.data.factory

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import lasori.komp.data.Convertible
import lasori.komp.data.generator.Generator

class JsonElementFactory(
    private val boolGenerator: Generator<Boolean>,
    private val intGenerator: Generator<Int>,
    private val doubleGenerator: Generator<Double>,
    private val stringGenerator: Generator<String>,
    private val customGenerators: List<Generator<Convertible<*, *>>>,
    private val json: Json
) : Factory<String, JsonElement> {

    override fun create(value: String): JsonElement {
        return when (value.lowercase()) {
            "boolean" -> JsonPrimitive(boolGenerator.generate())
            "numeric" -> JsonPrimitive(intGenerator.generate())
            "double" -> JsonPrimitive(doubleGenerator.generate())
            "string" -> JsonPrimitive(stringGenerator.generate())
            "array" -> JsonArray(emptyList())
            "object" -> JsonObject(emptyMap())
            "null" -> JsonNull
            else -> {
                val customGenerator = customGenerators.find { it.generate().type.lowercase() == value.lowercase() }
                customGenerator?.generate()?.toJsonElement(json) ?: JsonNull
            }
        }
    }
}
