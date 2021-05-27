import java.io.File
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

class Injector {

    @Target(AnnotationTarget.PROPERTY)
    annotation class InjectSetup
    annotation class InjectActions

    companion object {

        private val map: MutableMap<String, MutableList<KClass<*>>> = mutableMapOf()

        init {

            val scanner = Scanner(File("di.properties"))
            while(scanner.hasNextLine()) {

                val line = scanner.nextLine()
                val parts = line.split("=")

                val list = mutableListOf<KClass<*>>()

                if (parts[1].contains(',')) parts[1].split(',').forEach{ list.add(Class.forName(it).kotlin) }
                else list.add(Class.forName(parts[1]).kotlin)

                map[parts[0]] = list

            }
            scanner.close()

        }

        fun <T:Any> create(type: KClass<T>) : T {

            val o: T = type.createInstance()

            type.declaredMemberProperties.forEach {

                if(it.hasAnnotation<InjectSetup>()) {

                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val obj = map[key]!![0].createInstance()
                    (it as KMutableProperty<*>).setter.call(o, obj)

                } else if (it.hasAnnotation<InjectActions>()) {

                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    //val list = it.getter.call(o) as MutableList<WindowAction>
                    val list = mutableListOf<WindowAction>()
                    map[key]!!.forEach { action ->
                        val obj = action.createInstance()
                        list.add(obj as WindowAction)
                    }
                    (it as KMutableProperty<*>).setter.call(o, list)

                }

            }

            return o

        }

    }

}