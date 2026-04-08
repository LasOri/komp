package lasori.komp

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import lasori.komp.data.Convertible
import kotlin.reflect.KProperty1

class KompifyDsl<Value> {
    val predefinedValues = mutableMapOf<KProperty1<Value, *>, Convertible<*, *>>()

    @Suppress("UNCHECKED_CAST")
    infix fun <T : Any> KProperty1<Value, T>.withValue(value: T) {
        val serializer = serializerFor(value)
        predefinedValues[this] = Convertible(value, serializer as KSerializer<Any>)
    }

    private fun serializerFor(value: Any): KSerializer<*> {
        return when (value) {
            is String -> String.serializer()
            is Int -> Int.serializer()
            is Double -> Double.serializer()
            is Boolean -> Boolean.serializer()
            is Long -> Long.serializer()
            is Float -> Float.serializer()
            else -> throw IllegalArgumentException("No serializer found for type: ${value::class}. Use Komp.kompify with Convertible for complex types.")
        }
    }
}

inline fun <reified Value> kompify(block: KompifyDsl<Value>.() -> Unit = {}): Value {
    val dsl = KompifyDsl<Value>()
    dsl.block()
    return if (dsl.predefinedValues.isEmpty()) {
        Komp.kompify()
    } else {
        Komp.kompify(predefinedValues = dsl.predefinedValues)
    }
}
