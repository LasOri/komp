package lasori.komp.data.generator.random

class StringRandom: Random<String>{
    override fun next(): String {
        return kotlin.random.Random.nextBytes(10).toString()
    }
}
