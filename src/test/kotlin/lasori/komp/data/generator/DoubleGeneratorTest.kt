package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

class DoubleGeneratorTest {

    private var randomValue: Double = 3.14
    private lateinit var random: Random<Double>
    private lateinit var generator: Generator<Double>

    @BeforeTest
    fun setUp() {
        random = object: Random<Double> {
            override fun next(): Double {
                return randomValue
            }
        }
        generator = DoubleGenerator(random)
    }

    @Test
    fun generate() {
        assertEquals(3.14, generator.generate())
        randomValue = 2.71
        assertEquals(2.71, generator.generate())
    }
}
