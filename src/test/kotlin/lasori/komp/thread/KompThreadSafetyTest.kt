package lasori.komp.thread

import lasori.komp.Komp
import lasori.komp.data.Convertible
import lasori.komp.data.generator.Generator
import lasori.komp.testData.SimpleData
import lasori.komp.testData.TestEnum
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class KompThreadSafetyTest {

    private val testEnumGenerator = object : Generator<Convertible<*, *>> {
        override fun generate(): Convertible<*, *> {
            return Convertible(TestEnum.good, TestEnum.serializer())
        }
    }

    @Test
    fun `concurrent kompify calls do not throw exceptions`() {
        val threadCount = 8
        val barrier = CyclicBarrier(threadCount)
        val latch = CountDownLatch(threadCount)
        val errors = AtomicInteger(0)

        repeat(threadCount) {
            Thread {
                try {
                    barrier.await()
                    Komp.setup(
                        host = this,
                        seed = it.toLong(),
                        customGenerators = arrayOf(testEnumGenerator)
                    )
                    val result: SimpleData = Komp.kompify()
                    assert(result.text.isNotEmpty())
                } catch (e: Exception) {
                    errors.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }.start()
        }

        latch.await()
        assertEquals(0, errors.get(), "Expected no errors from concurrent kompify calls")
    }
}
