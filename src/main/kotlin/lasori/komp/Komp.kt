package lasori.komp

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import lasori.komp.annotation.Kompose
import lasori.komp.data.CodingKey
import lasori.komp.data.Convertible
import lasori.komp.data.extension.extractPath
import lasori.komp.data.extension.extractType
import lasori.komp.data.extension.update
import lasori.komp.data.factory.JsonElementFactory
import lasori.komp.data.generator.Generator
import lasori.komp.data.generator.GenericGenerator
import lasori.komp.data.generator.random.BoolRandom
import lasori.komp.data.generator.random.DoubleRandom
import lasori.komp.data.generator.random.IntRandom
import lasori.komp.data.generator.random.StringRandom
import lasori.komp.data.generator.valueType.DoubleType
import lasori.komp.data.generator.valueType.IntType
import lasori.komp.data.generator.valueType.StringType
import lasori.komp.function.loadResource
import lasori.komp.function.scanAnnotatedProperties
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

object Komp {

    private val stringGenerator = GenericGenerator(random = StringRandom())

    val json = Json {
        prettyPrint = false
    }
    var jsonDataFactory = JsonElementFactory(
        boolGenerator = GenericGenerator(random = BoolRandom()),
        intGenerator = GenericGenerator(random = IntRandom()),
        doubleGenerator = GenericGenerator(random = DoubleRandom()),
        stringGenerator = stringGenerator,
        customGenerators = emptyList(),
        json = json
    )
    private val predefinedValues = loadResource("values.json")?.let {
        this.json.parseToJsonElement(it)
    }

    fun setup(host: Any,
              intType: IntType = IntType.random,
              doubleType: DoubleType = DoubleType.random,
              stringType: StringType = StringType.random,
              vararg customGenerators: Generator<Convertible<*, *>>) {
        initJsonDataFactory(intType, doubleType, stringType, customGenerators)
        val set = scanAnnotatedProperties<Kompose>(host)
        set.forEach { property ->
            val serializer = serializer(property.returnType)
            val value = kompose(serializer)

            property.isAccessible = true
            property.setter.call(host, value)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified Value>kompose(predefinedValues: Map<KProperty1<Value, *>, Convertible<*, *>>? = null, vararg convertibles: Convertible<*, *>): Value {
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
                        val value = this.jsonValue(predefinedValues as Map<KProperty1<*, *>, Convertible<*, *>>?, it)
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

    fun jsonValue(predefinedValues: Map<KProperty1<*, *>, Convertible<*, *>>?, missingField: String): JsonElement {
        var value = jsonDataFactory.create("object")
        predefinedValues?.keys?.firstOrNull { key ->
            key.name.lowercase() == missingField.lowercase()
        }?.let { key ->
            value = predefinedValues[key]!!.toJsonElement(json)
        }
        return value
    }

    private inline fun <reified ValueType>predefinedList(key: String): List<ValueType>? {
        return predefinedValues?.jsonObject?.get(key)?.let {
            json.decodeFromJsonElement(it)
        }
    }

    private fun kompose(serializer: DeserializationStrategy<*>, predefinedValues: Map<KProperty1<*, *>, Convertible<*, *>>? = null, vararg convertibles: Convertible<*, *>): Any {
        var result: Any? = null
        var jsonData: JsonElement = JsonObject(emptyMap())
        do {
            try {
                val jsonString = this.json.encodeToString(jsonData)
                result = this.json.decodeFromString(serializer, jsonString)
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

    private fun initJsonDataFactory(
        intType: IntType,
        doubleType: DoubleType,
        stringType: StringType,
        customGenerators: Array<out Generator<Convertible<*, *>>>
    ) {
        val intGenerator = when (intType) {
            IntType.random -> GenericGenerator(random = IntRandom())
            IntType.prime -> GenericGenerator(
                random = IntRandom(),
                preparedValues = predefinedList("primeNumbers")
            )

            IntType.fibonacci -> GenericGenerator(
                random = IntRandom(),
                preparedValues = predefinedList("fibonacciNumbers")
            )
        }
        val doubleGenerator = when (doubleType) {
            DoubleType.random -> GenericGenerator(random = DoubleRandom())
            DoubleType.famousConstants -> GenericGenerator(
                random = DoubleRandom(),
                preparedValues = predefinedList("famousConstants")
            )
        }
        val stringGenerator = when (stringType) {
            StringType.random -> GenericGenerator(random = StringRandom())
            StringType.movieQuote -> GenericGenerator(
                random = StringRandom(),
                preparedValues = predefinedList("movieQuotes")
            )

            StringType.movieHero -> GenericGenerator(
                random = StringRandom(),
                preparedValues = predefinedList("movieHeroes")
            )

            StringType.movieVillain -> GenericGenerator(
                random = StringRandom(),
                preparedValues = predefinedList("movieVillains")
            )
        }
        jsonDataFactory = JsonElementFactory(
            boolGenerator = GenericGenerator(random = BoolRandom()),
            intGenerator = intGenerator,
            doubleGenerator = doubleGenerator,
            stringGenerator = stringGenerator,
            customGenerators = customGenerators.toList(),
            json = json
        )
    }
}
