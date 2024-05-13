package lasori.komp

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import lasori.komp.data.CodingKey
import lasori.komp.data.Serializable
import lasori.komp.data.extension.extractPath
import lasori.komp.data.extension.extractType
import lasori.komp.data.extension.update
import lasori.komp.data.factory.JsonElementFactory
import lasori.komp.data.factory.JsonType
import lasori.komp.data.generator.BoolGenerator
import lasori.komp.data.generator.DoubleGenerator
import lasori.komp.data.generator.IntGenerator
import lasori.komp.data.generator.StringGenerator
import lasori.komp.data.generator.random.BoolRandom
import lasori.komp.data.generator.random.DoubleRandom
import lasori.komp.data.generator.random.IntRandom
import lasori.komp.data.generator.random.StringRandom

class Komp {

    private val stringGenerator = StringGenerator(random = StringRandom())

    val json = Json {
        prettyPrint = false
    }
    val jsonDataFactory = JsonElementFactory(
        boolGenerator = BoolGenerator(random = BoolRandom()),
        intGenerator = IntGenerator(random = IntRandom()),
        doubleGenerator = DoubleGenerator(random = DoubleRandom()),
        stringGenerator = stringGenerator
    )

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified Value>kompose(vararg serializable: Serializable<*, *>): Value {
        var result: Value? = null
        var jsonData: JsonElement = JsonObject(emptyMap())
        do {
            try {
                val jsonString = this.json.encodeToString(jsonData)
                result = this.json.decodeFromString(jsonString)
            } catch (e: MissingFieldException) {
                e.extractPath()?.let { path ->
                    e.missingFields.forEach {
                        val newPath = path.toMutableList().apply {
                            add(CodingKey(it))
                        }
                        val updatedJsonData =  jsonData.update(newPath, jsonDataFactory.create(JsonType.`object`))
                        jsonData = updatedJsonData
                    }
                }
            } catch (e: Exception) {
                val path = e.extractPath()
                val type = e.extractType()

                val jsonElement: JsonElement? = serializable.firstOrNull {
                    it.type == type
                }?.toJsonElement(json)
                print("Exception: $e, path: $path, type: $type, jsonElement: $jsonElement \n")
                if (path != null && type != null) {
                    val value = jsonElement ?: jsonDataFactory.create(JsonType.fromString(type))
                    val newJsonData = jsonData.update(path, value)
                    jsonData = newJsonData as JsonObject
                } else {
                    println("Lofasz: \n")
                }
            }
        } while (result == null)
        return result
    }

}