package lasori.komp

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import lasori.komp.data.CodingKey
import lasori.komp.data.Convertible
import lasori.komp.data.extension.extractPath
import lasori.komp.data.extension.extractType
import lasori.komp.data.extension.update
import lasori.komp.data.factory.JsonElementFactory
import kotlin.reflect.KProperty1

class Kompifier(private val json: Json,
                private val jsonDataFactory: JsonElementFactory) {

    @OptIn(ExperimentalSerializationApi::class)
    fun kompify(predefinedValues: Map<KProperty1<*, *>, Convertible<*, *>>? = null,
                convertibles: List<Convertible<*, *>>,
                jsonizer: (json: Json, jsonData: JsonElement) -> (Any)): Any {
        var result: Any? = null
        var jsonData: JsonElement = JsonObject(emptyMap())
        do {
            try {
                result = jsonizer(json, jsonData)
            } catch (e: MissingFieldException) {
                e.extractPath()?.let { path ->
                    e.missingFields.forEach {
                        val newPath = path.toMutableList().apply {
                            add(CodingKey(it))
                        }
                        val value = this.jsonValue(predefinedValues, it)
                        val updatedJsonData =  jsonData.update(newPath, value)
                        jsonData = updatedJsonData
                    }
                }
            } catch (e: Exception) {
                val path = e.extractPath()
                val type = e.extractType()

                val jsonElement: JsonElement? = convertibles.firstOrNull {
                    it.type == type
                }?.toJsonElement(json)
                if (path != null && type != null) {
                    val value = jsonElement ?: jsonDataFactory.create(type)
                    val newJsonData = jsonData.update(path, value)
                    jsonData = newJsonData as JsonObject
                } else {
                    e.printStackTrace()
                }
            }
        } while (result == null)
        return result
    }

    private fun jsonValue(predefinedValues: Map<KProperty1<*, *>, Convertible<*, *>>?, missingField: String): JsonElement {
        var value = jsonDataFactory.create("object")
        predefinedValues?.keys?.firstOrNull { key ->
            key.name.lowercase() == missingField.lowercase()
        }?.let { key ->
            value = predefinedValues[key]!!.toJsonElement(json)
        }
        return value
    }

}
