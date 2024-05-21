package lasori.komp.function

import java.io.File
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

fun loadResource(name: String): String? = {}::class.java.classLoader.getResource(name)?.readText()

inline fun <reified AnnotationType>scanAnnotatedProperties(host: Any): Set<KMutableProperty<*>> where AnnotationType: Annotation {
    val packageName = host.javaClass.packageName
    return host::class.java.classLoader
        .resources(packageName.replace(".", "/"))
        .map {
            File(it.file)
                .walkTopDown()
                .filter { file ->
                    file.isFile
                }
                .toList()
        }
        .toList()
        .flatten()
        .map {
            val clazz = it.path.substring(it.path.indexOf(packageName.replace(".", "/"))).substringBefore(".").substringBefore("$").replace("/", ".")
            Class.forName(clazz)
        }
        .map {
            it.kotlin.declaredMemberProperties
                .filter { property ->
                    property.findAnnotation<AnnotationType>() != null && property is KMutableProperty<*>
                }
                .map { property ->
                    property as KMutableProperty<*>
                }
        }
        .flatten()
        .toSet()
}
