package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random

class DoubleGenerator(private val random: Random<Double>): Generator<Double> {
    override fun generate(): Double {
        return random.next()
    }
}