package lasori.komp.data.factory

interface Factory<Value, ResultType> {

    fun create(value: Value): ResultType

}
