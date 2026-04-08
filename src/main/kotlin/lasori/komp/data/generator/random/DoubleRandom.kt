package lasori.komp.data.generator.random

class DoubleRandom(seed: Long? = null): Random<Double> {
    private val random = seed?.let { kotlin.random.Random(it) } ?: kotlin.random.Random

    override fun next(): Double {
        return random.nextDouble()
    }
}