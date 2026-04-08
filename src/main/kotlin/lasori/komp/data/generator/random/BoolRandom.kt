package lasori.komp.data.generator.random

class BoolRandom(seed: Long? = null): Random<Boolean> {
    private val random = seed?.let { kotlin.random.Random(it) } ?: kotlin.random.Random

    override fun next(): Boolean {
        return random.nextBoolean()
    }
}
