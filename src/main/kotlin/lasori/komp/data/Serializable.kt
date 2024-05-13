package lasori.komp.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class Serializable<Serializable, Serializer>(private val serializable: Serializable, private val serializer: Serializer) where
Serializer: KSerializer<Serializable> {

    val type: String by lazy {
        this.serializable?.let {it::class.java.simpleName } ?: throw Exception("Could not get type from serializable")
    }

    fun toJsonElement(json: Json): JsonElement {
        return json.encodeToJsonElement(serializer, serializable)
    }

}
