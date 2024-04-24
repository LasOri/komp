package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random

class IntGenerator(private val random: Random<Int>): Generator<Int>{
    override fun generate(): Int {
        return random.next()
    }
}
