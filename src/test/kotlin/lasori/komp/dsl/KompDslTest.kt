package lasori.komp.dsl

import kotlinx.serialization.builtins.serializer
import lasori.komp.Komp
import lasori.komp.data.Convertible
import lasori.komp.data.generator.Generator
import lasori.komp.kompify
import lasori.komp.testData.SimpleData
import lasori.komp.testData.TestEnum
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KompDslTest {

    private val testEnumGenerator = object : Generator<Convertible<*, *>> {
        override fun generate(): Convertible<*, *> {
            return Convertible(TestEnum.good, TestEnum.serializer())
        }
    }

    @BeforeTest
    fun setUp() {
        Komp.setup(host = this, seed = 42L, customGenerators = arrayOf(testEnumGenerator))
    }

    @Test
    fun `kompify DSL with predefined text`() {
        val result: SimpleData = kompify {
            SimpleData::text withValue "hello"
        }

        assertEquals("hello", result.text)
        assertNotNull(result.num)
    }

    @Test
    fun `kompify DSL with multiple predefined values`() {
        val result: SimpleData = kompify {
            SimpleData::text withValue "hello"
            SimpleData::num withValue 99
        }

        assertEquals("hello", result.text)
        assertEquals(99, result.num)
    }

    @Test
    fun `kompify DSL without any predefined values`() {
        val result: SimpleData = kompify {}

        assertNotNull(result.text)
        assertNotNull(result.num)
    }
}
