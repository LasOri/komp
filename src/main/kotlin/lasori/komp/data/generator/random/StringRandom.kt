package lasori.komp.data.generator.random

class StringRandom(seed: Long? = null): Random<String> {
    private val random = seed?.let { kotlin.random.Random(it) } ?: kotlin.random.Random
    private val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun next(): String {
        return (1..10).map { chars[random.nextInt(chars.size)] }.joinToString("")
    }
}
