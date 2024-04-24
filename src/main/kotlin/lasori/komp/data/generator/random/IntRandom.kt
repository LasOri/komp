package lasori.komp.data.generator.random

class IntRandom: Random<Int> {
    override fun next(): Int {
        return kotlin.random.Random.nextInt()
    }
}