package lasori.komp

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import lasori.komp.annotation.Kompify
import lasori.komp.data.Convertible
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

    private val json = Json {
        prettyPrint = false
    }
    private var jsonDataFactory = JsonElementFactory(
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

    val kompifier = Kompifier(json, jsonDataFactory)

    fun setup(host: Any,
              intType: IntType = IntType.random,
              doubleType: DoubleType = DoubleType.random,
              stringType: StringType = StringType.random,
              vararg customGenerators: Generator<Convertible<*, *>>) {
        initJsonDataFactory(intType, doubleType, stringType, customGenerators)
        val set = scanAnnotatedProperties<Kompify>(host)
        set.forEach { property ->
            val serializer = serializer(property.returnType)
            val value = kompify(serializer)

            property.isAccessible = true
            property.setter.call(host, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified Value>kompify(predefinedValues: Map<KProperty1<Value, *>, Convertible<*, *>>? = null, vararg convertibles: Convertible<*, *>): Value {
        return this.kompifier.kompify(predefinedValues as Map<KProperty1<*, *>, Convertible<*, *>>?, convertibles.toList()) { json, jsonData ->
            val jsonString = json.encodeToString(jsonData)
            val value: Value = json.decodeFromString(jsonString)
            value as Any
        }  as Value
    }

    private fun kompify(serializer: DeserializationStrategy<*>, predefinedValues: Map<KProperty1<*, *>, Convertible<*, *>>? = null, vararg convertibles: Convertible<*, *>): Any {
        return this.kompifier.kompify(predefinedValues, convertibles.toList()) { json, jsonData ->
            val jsonString = json.encodeToString(jsonData)
            json.decodeFromString(serializer, jsonString)!!
        }
    }

    private inline fun <reified ValueType>predefinedList(key: String): List<ValueType>? {
        return predefinedValues?.jsonObject?.get(key)?.let {
            json.decodeFromJsonElement(it)
        }
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
