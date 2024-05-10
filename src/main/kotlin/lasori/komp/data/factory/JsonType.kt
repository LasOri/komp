package lasori.komp.data.factory

enum class JsonType {
    boolean,
    int,
    double,
    string,
    array,
    `object`,
    `null`;

    companion object {
        fun fromString(type: String) = when (type.lowercase()) {
            "boolean" -> boolean
            "numeric" -> int
            "double" -> double
            "string" -> string
            "array" -> array
            "object" -> `object`
            "null" -> `null`
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

}
