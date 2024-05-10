package lasori.komp

import lasori.komp.testData.TestData
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

class KompTest {

    lateinit var komp: Komp

    @BeforeTest
    fun setUp() {
        komp = Komp()
    }

    @Test
    fun testKomp() = run {
        val testData: TestData = komp.kompose()
        println("TestData: $testData")

        assertNotNull(testData)
    }

}
