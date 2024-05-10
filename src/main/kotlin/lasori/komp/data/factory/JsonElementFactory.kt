package lasori.komp.data.factory

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import lasori.komp.data.factory.JsonType.*
import lasori.komp.data.generator.Generator

class JsonElementFactory(
    private val boolGenerator: Generator<Boolean>,
    private val intGenerator: Generator<Int>,
    private val doubleGenerator: Generator<Double>,
    private val stringGenerator: Generator<String>
) : Factory<JsonType, JsonElement> {

    override fun create(value: JsonType): JsonElement {
        return when (value) {
            boolean -> JsonPrimitive(boolGenerator.generate())
            int -> JsonPrimitive(intGenerator.generate())
            double -> JsonPrimitive(doubleGenerator.generate())
            string -> JsonPrimitive(stringGenerator.generate())
            array -> JsonArray(emptyList())
            `object` -> JsonObject(emptyMap())
            `null` -> JsonNull
        }
    }
}
