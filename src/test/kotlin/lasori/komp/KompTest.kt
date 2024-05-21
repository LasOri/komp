package lasori.komp

import kotlinx.serialization.builtins.serializer
import lasori.komp.annotation.Kompose
import lasori.komp.data.Convertible
import lasori.komp.data.generator.Generator
import lasori.komp.data.generator.valueType.DoubleType
import lasori.komp.data.generator.valueType.IntType
import lasori.komp.data.generator.valueType.StringType
import lasori.komp.testData.TestData
import lasori.komp.testData.TestEnum
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class KompTest {

    lateinit var komp: Komp

    @Kompose
    lateinit var testData: TestData

    @BeforeTest
    fun setUp() {
        val testEnumGenerator = object : Generator<Convertible<*, *>> {
            override fun generate(): Convertible<*, *> {
                val testEnum = TestEnum.entries.toList().random()
                return Convertible(testEnum, TestEnum.serializer())
            }
        }
        komp = Komp
        komp.setup(
            host = this,
            intType = IntType.prime,
            doubleType = DoubleType.famousConstants,
            stringType = StringType.movieQuote,
            testEnumGenerator)
    }

    @Test
    fun testKompose() = run {
        val expectedValue = "TestText"

        val testData: TestData = komp.kompose(predefinedValues = mapOf(TestData::text to Convertible(expectedValue, String.serializer())))

        println("\n\n$testData\n\n".trimIndent())

        assertNotNull(testData)
        assertEquals(expectedValue, testData.text)
    }

    @Test
    fun testKompose_withAnnotation() = run {
        assertNotNull(testData)
    }

}
