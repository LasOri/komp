package lasori.komp.data.generator.random

import kotlin.test.Test
import kotlin.test.assertEquals

class SeededRandomTest {

    @Test
    fun `BoolRandom with same seed produces same sequence`() {
        val random1 = BoolRandom(seed = 42L)
        val random2 = BoolRandom(seed = 42L)

        val results1 = (1..10).map { random1.next() }
        val results2 = (1..10).map { random2.next() }

        assertEquals(results1, results2)
    }

    @Test
    fun `IntRandom with same seed produces same sequence`() {
        val random1 = IntRandom(seed = 42L)
        val random2 = IntRandom(seed = 42L)

        val results1 = (1..10).map { random1.next() }
        val results2 = (1..10).map { random2.next() }

        assertEquals(results1, results2)
    }

    @Test
    fun `DoubleRandom with same seed produces same sequence`() {
        val random1 = DoubleRandom(seed = 42L)
        val random2 = DoubleRandom(seed = 42L)

        val results1 = (1..10).map { random1.next() }
        val results2 = (1..10).map { random2.next() }

        assertEquals(results1, results2)
    }

    @Test
    fun `StringRandom with same seed produces same sequence`() {
        val random1 = StringRandom(seed = 42L)
        val random2 = StringRandom(seed = 42L)

        val results1 = (1..10).map { random1.next() }
        val results2 = (1..10).map { random2.next() }

        assertEquals(results1, results2)
    }

    @Test
    fun `StringRandom produces readable alphanumeric strings`() {
        val random = StringRandom(seed = 42L)
        val result = random.next()

        assert(result.all { it.isLetterOrDigit() }) {
            "Expected alphanumeric string but got: $result"
        }
        assert(result.length == 10) {
            "Expected length 10 but got: ${result.length}"
        }
    }

    @Test
    fun `default constructors still work without seed`() {
        val boolRandom = BoolRandom()
        val intRandom = IntRandom()
        val doubleRandom = DoubleRandom()
        val stringRandom = StringRandom()

        boolRandom.next()
        intRandom.next()
        doubleRandom.next()
        stringRandom.next()
    }
}
