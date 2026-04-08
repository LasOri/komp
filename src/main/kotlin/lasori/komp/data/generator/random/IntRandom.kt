package lasori.komp.data.generator.random

class IntRandom(seed: Long? = null): Random<Int> {
    private val random = seed?.let { kotlin.random.Random(it) } ?: kotlin.random.Random

    override fun next(): Int {
        return random.nextInt()
    }
}