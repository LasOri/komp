package lasori.komp.data

import kotlinx.serialization.Serializable

@Serializable
sealed class JsonData {
    object Null: JsonData()
    data class Boolean(val value: kotlin.Boolean): JsonData()
    data class Number(val value: kotlin.Number): JsonData()
    data class String(val value: kotlin.String): JsonData()
    data class Array(val value: kotlin.collections.List<JsonData>): JsonData()
    data class Object(val value: kotlin.collections.Map<kotlin.String, JsonData>): JsonData()
}
