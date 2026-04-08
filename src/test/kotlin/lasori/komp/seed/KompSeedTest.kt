package lasori.komp.seed

import kotlinx.serialization.builtins.serializer
import lasori.komp.Komp
import lasori.komp.data.Convertible
import lasori.komp.data.generator.Generator
import lasori.komp.testData.SimpleData
import lasori.komp.testData.TestEnum
import kotlin.test.Test
import kotlin.test.assertEquals

class KompSeedTest {

    private val testEnumGenerator = object : Generator<Convertible<*, *>> {
        override fun generate(): Convertible<*, *> {
            return Convertible(TestEnum.good, TestEnum.serializer())
        }
    }

    @Test
    fun `kompify with same seed produces same result`() {
        Komp.setup(host = this, seed = 42L, customGenerators = arrayOf(testEnumGenerator))
        val result1: SimpleData = Komp.kompify()

        Komp.setup(host = this, seed = 42L, customGenerators = arrayOf(testEnumGenerator))
        val result2: SimpleData = Komp.kompify()

        assertEquals(result1, result2)
    }

    @Test
    fun `kompify without seed still works`() {
        Komp.setup(host = this, customGenerators = arrayOf(testEnumGenerator))
        val result: SimpleData = Komp.kompify()

        assert(result.text.isNotEmpty())
    }
}
