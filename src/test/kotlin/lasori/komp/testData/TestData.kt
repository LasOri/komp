package lasori.komp.testData

import kotlinx.serialization.Serializable

@Serializable
data class TestData(
    val text: String,
    val num: Int,
    val floating: Double,
    val bool: Boolean,
    val map: Map<String, String>,
    val list: List<String>,
    val innerData: InnerData,
    val innerList: List<InnerData>,
    val innerMap: Map<String, InnerData>,
    val optional: String?
)

@Serializable
data class InnerData(
    val subData: SubData,
    val num: Int,
    val floating: Double,
    val bool: Boolean,
    val map: Map<String, String>,
    val list: List<String>
)

@Serializable
data class SubData(
    val text: String
)
