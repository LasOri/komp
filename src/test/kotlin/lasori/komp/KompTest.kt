package lasori.komp

import lasori.komp.data.Serializable
import lasori.komp.testData.TestData
import lasori.komp.testData.TestEnum
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
    fun testKompose_withSerializable() = run {
        val testData: TestData = komp.kompose(Serializable(TestEnum.ugly, TestEnum.serializer()))

        assertNotNull(testData)
    }

}
