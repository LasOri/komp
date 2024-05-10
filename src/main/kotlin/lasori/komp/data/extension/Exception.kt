package lasori.komp.data.extension

import lasori.komp.data.CodingKey
import lasori.komp.data.factory.JsonType

fun Exception.extractPath(): List<CodingKey>? {
    val regexp = "at path: (.*)".toRegex()
    val matches = message?.let {
        val match = regexp.find(it)
        match?.groupValues?.get(1)?.split(".")
    }
    return matches?.let {
        it.drop(1).map { key ->
            CodingKey(key = key)
        }
    }
}

fun Exception.extractType(): JsonType? {
    val type = this.message?.wordBefore("literal") ?:
    this.message?.wordBefore("for input") ?:
    this.message?.wordBefore("but got") ?:
    this.message?.arrayType() ?:
    this.message?.objectType()
    return type?.let {
        JsonType.fromString(it)
    }
}

private fun String.wordBefore(beforeText: String): String? {
    val regexp = "\\b\\W?(\\w+)\\W*\\b(?=$beforeText)".toRegex()
    val match = regexp.find(this)
    return match?.groupValues?.last()
}

private fun String.arrayType(): String? = if (this.contains("'['")) {
    "array"
} else null

private fun String.objectType(): String? = if (this.contains("'{'")) {
    "object"
} else null
