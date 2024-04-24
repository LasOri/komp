package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

class StringGeneratorTest {

    private var randomValue: String = "testValue"
    private lateinit var random: Random<String>
    private lateinit var generator: Generator<String>

    @BeforeTest
    fun setUp() {
        random = object: Random<String> {
            override fun next(): String {
                return randomValue
            }
        }
        generator = StringGenerator(random)
    }

    @Test
    fun generate() {
        assertEquals("testValue", generator.generate())
        randomValue = "newTestValue"
        assertEquals("newTestValue", generator.generate())
    }
}
