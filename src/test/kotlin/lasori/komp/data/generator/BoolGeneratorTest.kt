package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

class BoolGeneratorTest {

    private var randomValue: Boolean = true
    private lateinit var random: Random<Boolean>
    private lateinit var boolGenerator: BoolGenerator

    @BeforeTest
    fun setUp() {
        random = object: Random<Boolean> {
            override fun next(): Boolean {
                return randomValue
            }
        }
        boolGenerator = BoolGenerator(random)
    }

    @Test
    fun generate() {
        randomValue = true
        assert(boolGenerator.generate())
        randomValue = false
        assert(!boolGenerator.generate())
    }
}
