package lasori.komp.data.generator.random

class DoubleRandom: Random<Double> {
    override fun next(): Double {
        return kotlin.random.Random.nextDouble()
    }
}