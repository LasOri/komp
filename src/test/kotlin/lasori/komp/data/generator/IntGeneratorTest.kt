package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random
import org.junit.jupiter.api.Assertions.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class IntGeneratorTest {

    private var randomValue: Int = 42
    private lateinit var random: Random<Int>
    private lateinit var generator: Generator<Int>

    @BeforeTest
    fun setUp() {
        random = object : Random<Int> {
            override fun next(): Int {
                return randomValue
            }
        }
        generator = IntGenerator(random)
    }

    @Test
    fun generate() {
        assertEquals(42, generator.generate())
        randomValue = 2
        assertEquals(2, generator.generate())
    }
}
