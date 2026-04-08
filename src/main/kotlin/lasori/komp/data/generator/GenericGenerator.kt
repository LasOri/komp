package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random

class GenericGenerator<ValueType>(private val random: Random<ValueType>,
                                  private val preparedValues: List<ValueType>? = null,
                                  private val seed: Long? = null): Generator<ValueType> {
    private val kotlinRandom = seed?.let { kotlin.random.Random(it) } ?: kotlin.random.Random

    override fun generate(): ValueType {
        return preparedValues?.random(kotlinRandom) ?: random.next()
    }

}
