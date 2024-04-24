package lasori.komp.data.generator.random

class BoolRandom: Random<Boolean> {
    override fun next(): Boolean {
        return kotlin.random.Random.nextBoolean()
    }
}
