package lasori.komp.function

import java.io.File
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

fun loadResource(name: String): String? = {}::class.java.classLoader.getResource(name)?.readText()

inline fun <reified AnnotationType>scanAnnotatedProperties(host: Any): Set<KMutableProperty<*>> where AnnotationType: Annotation {
    val packageName = host.javaClass.packageName
    return filesOfHost(host, packageName)
        .map { classFromFile(it, packageName) }
        .flatMap { annotatedPropertiesFromClass<AnnotationType>(it) }
        .toSet()
}

inline fun <reified AnnotationType> annotatedPropertiesFromClass(it: Class<*>) where AnnotationType : Annotation =
    it.kotlin.declaredMemberProperties
        .filter { property ->
            property.findAnnotation<AnnotationType>() != null && property is KMutableProperty<*>
        }
        .map { property ->
            property as KMutableProperty<*>
        }

fun classFromFile(it: File, packageName: String): Class<*> {
    val clazz =
        it.path.substring(it.path.indexOf(packageName.replace(".", "/"))).substringBefore(".")
            .substringBefore("$").replace("/", ".")
    return Class.forName(clazz)
}

fun filesOfHost(host: Any, packageName: String) = host::class.java.classLoader
    .resources(packageName.replace(".", "/"))
    .toList()
    .flatMap {
        File(it.file)
            .walkTopDown()
            .filter { file ->
                file.isFile
            }
            .toList()
    }
