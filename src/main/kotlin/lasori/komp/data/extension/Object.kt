package lasori.komp.data.extension

fun loadResource(name: String): String? = {}::class.java.classLoader.getResource(name)?.readText()