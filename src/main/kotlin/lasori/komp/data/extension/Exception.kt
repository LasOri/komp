package lasori.komp.data.extension

import lasori.komp.data.CodingKey
import lasori.komp.data.factory.JsonType

fun Exception.extractPath(): List<CodingKey>? {
    val matches = message?.wordAfter("path")?.split(".")
    return matches?.let {
        it.drop(1).map { key ->
            CodingKey(key = key)
        }
    }
}

fun Exception.extractType(): String? {
    return this.message?.wordBefore("literal") ?:
    this.message?.wordBefore("for input") ?:
    this.message?.wordBefore("but got") ?:
    this.message?.wordBefore("does not") ?:
    this.message?.arrayType() ?:
    this.message?.objectType()
}

private fun String.wordBefore(text: String): String? {
    val regexp = "\\b\\W?(\\w+)\\W*\\b(?=$text)".toRegex()
    val match = regexp.find(this)
    return match?.groupValues?.last()
}

private fun String.wordAfter(text: String): String? {
    val regexp = "(?<=$text)\\W?\\s?(.*)".toRegex()
    val match = regexp.find(this)
    return match?.groupValues?.last()
}

private fun String.arrayType(): String? = if (this.contains("'['")) {
    "array"
} else null

private fun String.objectType(): String? = if (this.contains("'{'")) {
    "object"
} else null
