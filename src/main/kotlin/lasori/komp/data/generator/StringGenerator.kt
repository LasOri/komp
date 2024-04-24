package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random

class StringGenerator(private val random: Random<String>): Generator<String> {
    override fun generate(): String {
        return random.next()
    }
}
