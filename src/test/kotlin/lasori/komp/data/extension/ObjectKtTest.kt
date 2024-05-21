package lasori.komp.data.extension

import lasori.komp.function.loadResource
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class ObjectKtTest {

    @Test
    fun testLoadResource() {
        val expected = "TestText"

        val result = loadResource("test.txt")

        assertEquals(expected, result)
    }

}
