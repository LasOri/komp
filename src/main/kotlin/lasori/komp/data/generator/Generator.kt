package lasori.komp.data.generator

interface Generator<ValueType> {

    fun generate(): ValueType
}
