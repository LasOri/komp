package lasori.komp.data.generator

import lasori.komp.data.generator.random.Random

class GenericGenerator<ValueType>(private val random: Random<ValueType>,
                                  private val preparedValues: List<ValueType>? = null): Generator<ValueType> {
    override fun generate(): ValueType {
        return preparedValues?.random() ?: random.next()
    }

}
