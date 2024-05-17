package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class GenericGeneratorTest {

    @Test
    fun testGenerate_shouldReturnRandomValue() {
        val random = object : Random<Int> {
            override fun next(): Int {
                return 2
            }
        }
        val generator = GenericGenerator(random)

        val result = generator.generate()

        assertEquals(2, result)
    }

    @Test
    fun testGenerate_shouldReturnPreparedValue() {
        val random = object : Random<Int> {
            override fun next(): Int {
                return 2
            }
        }
        val generator = GenericGenerator(random, listOf(3))

        val result = generator.generate()

        assertEquals(3, result)
    }

}
