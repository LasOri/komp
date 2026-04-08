package lasori.komp.collection

import lasori.komp.Komp
import lasori.komp.data.Convertible
import lasori.komp.data.generator.Generator
import lasori.komp.testData.TestEnum
import kotlin.test.Test
import kotlin.test.assertTrue

class CollectionGenerationTest {

    private val testEnumGenerator = object : Generator<Convertible<*, *>> {
        override fun generate(): Convertible<*, *> {
            return Convertible(TestEnum.good, TestEnum.serializer())
        }
    }

    @kotlinx.serialization.Serializable
    data class WithList(val items: List<String>, val name: String)

    @kotlinx.serialization.Serializable
    data class WithMap(val entries: Map<String, String>, val name: String)

    @Test
    fun `kompify generates non-empty lists when collectionSize is set`() {
        Komp.setup(host = this, seed = 42L, collectionSize = 3, customGenerators = arrayOf(testEnumGenerator))

        val result: WithList = Komp.kompify()

        assertTrue(result.items.size == 3, "Expected 3 items but got ${result.items.size}")
    }

    @Test
    fun `kompify generates non-empty maps when collectionSize is set`() {
        Komp.setup(host = this, seed = 42L, collectionSize = 2, customGenerators = arrayOf(testEnumGenerator))

        val result: WithMap = Komp.kompify()

        assertTrue(result.entries.size == 2, "Expected 2 entries but got ${result.entries.size}")
    }

    @Test
    fun `kompify generates empty collections when collectionSize is 0`() {
        Komp.setup(host = this, seed = 42L, collectionSize = 0, customGenerators = arrayOf(testEnumGenerator))

        val result: WithList = Komp.kompify()

        assertTrue(result.items.isEmpty(), "Expected empty list")
    }

    @Test
    fun `kompify defaults to empty collections when collectionSize not set`() {
        Komp.setup(host = this, seed = 42L, customGenerators = arrayOf(testEnumGenerator))

        val result: WithList = Komp.kompify()

        assertTrue(result.items.isEmpty(), "Expected empty list by default")
    }
}
