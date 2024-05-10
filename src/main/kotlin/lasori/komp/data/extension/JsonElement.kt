package lasori.komp.data.extension

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import lasori.komp.data.CodingKey

operator fun JsonElement.get(path: List<CodingKey>): JsonElement? {
    val jsonElement: JsonElement? = path.firstOrNull()?.let {
         when (this) {
            is JsonObject -> it.key?.let { key -> this[key] }
            is JsonArray -> it.index?.let { index -> this.getOrNull(index) }
            else -> null
        }
    }
    return if (jsonElement != null) {
        return if (path.count() > 1) {
            jsonElement[path.drop(1)]
        } else { jsonElement }
    } else {
        null
    }
}

fun JsonElement.update(path: List<CodingKey>, value: JsonElement?): JsonElement {
    val codingKey = path.firstOrNull() ?: return this
    return if (path.count() > 1) {
        val updatedPath = path.drop(1)
        val jsonValue = this[listOf(codingKey)]
        val updatedJsonValue = jsonValue?.update(updatedPath, value)
        this.update(listOf(codingKey), updatedJsonValue)
    } else {
        when (this) {
            is JsonObject -> {
                val jsonMap = this.toMutableMap()
                val updatedJsonMap = codingKey.key?.let {
                    if (value == null) {
                        jsonMap.remove(it)
                        jsonMap
                    } else {
                        jsonMap[it] = value
                        jsonMap
                    }
                } ?: throw IllegalArgumentException("JsonObject updating failed")
                return JsonObject(updatedJsonMap)
            }
            is JsonArray -> {
                val jsonList = this.toMutableList()
                val updatedJsonList = codingKey.index?.let {
                    if (value == null) {
                        jsonList.removeAt(it)
                        jsonList
                    } else {
                        jsonList[it] = value
                        jsonList
                    }
                } ?: throw IllegalArgumentException("JsonArray updating failed")
                return JsonArray(updatedJsonList)
            }
            else -> throw IllegalArgumentException("JsonElement updating failed")
        }
    }
}
