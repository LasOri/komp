package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random

class BoolGenerator(private val random: Random<Boolean>): Generator<Boolean> {
    override fun generate(): Boolean {
        return random.next()
    }
}
