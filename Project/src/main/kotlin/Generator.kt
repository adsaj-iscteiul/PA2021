import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class Generator() {

    val JSON_Files: MutableMap<Any, JSON_Value> = mutableMapOf()

    fun generateJSON(dataclassInstance: Any) {

        val dataclass: KClass<*> = dataclassInstance::class as KClass<*>

        var fileName = dataclassInstance::class.simpleName + "JSON"
        val fileValue = toJSON(dataclassInstance)
        val fileContent = fileValue.serialize()

        var sameClassFiles = 0
        var fileFound = false
        for (f in JSON_Files) {

            if (f.key == dataclassInstance) {

                fileFound = true
                JSON_Files[f.key] = toJSON(dataclassInstance)
                fileName += (sameClassFiles + 1)

            }

            if (f.key::class == dataclass) {
                sameClassFiles++
            }

        }
        if (!fileFound) {
            JSON_Files.put(dataclassInstance, fileValue)
            fileName += (sameClassFiles + 1)
        }

        val path = Paths.get("").toAbsolutePath().toString() + "\\JSON_Files"
        val file = File(path, fileName + ".json")

        file.delete()
        file.createNewFile()
        file.appendText(fileContent)

    }

    fun toJSON(instance: Any?): JSON_Value {

        var instanceJSON: JSON_Value = JSON_Null()

        if (instance != null) {

            if (instance::class.isData) {

                instanceJSON = JSON_Object()

                val instanceClass: KClass<*> = instance::class as KClass<*>

                instanceClass.declaredMemberProperties.sortedBy {

                    it.findAnnotation<Index>()!!.index

                }.forEach {

                    if (!it.hasAnnotation<Ignore>()) {

                        var keyID = ""
                        if (it.hasAnnotation<Identifier>()) {
                            keyID = it.findAnnotation<Identifier>()!!.identifier
                        } else {
                            keyID = it.name
                        }

                        val key = toJSON(keyID)
                        val value = toJSON(it.getter.call(instance))
                        (instanceJSON as JSON_Object).addToObject(key as JSON_String, value)

                    }

                }

            } else {

                when (instance) {

                    is Collection<*> -> {

                        instanceJSON = JSON_Array()

                        var collectionFields = 0
                        while (collectionFields < instance.size) {

                            val fieldElement = toJSON(instance.elementAt(collectionFields))
                            instanceJSON.addToArray(fieldElement)

                            collectionFields++

                        }

                    }

                    is Map<*,*> -> {

                        instanceJSON = JSON_Object()

                        var mapFields = 0
                        while (mapFields < instance.size) {

                            val fieldKey = toJSON(instance.keys.elementAt(mapFields).toString())
                            val fieldValue = toJSON(instance.values.elementAt(mapFields))
                            instanceJSON.addToObject(fieldKey as JSON_String, fieldValue)

                            mapFields++

                        }

                    }

                    is Byte -> instanceJSON = JSON_Number(instance.toDouble())
                    is Short -> instanceJSON = JSON_Number(instance.toDouble())
                    is Int -> instanceJSON = JSON_Number(instance.toDouble())
                    is Long -> instanceJSON = JSON_Number(instance.toDouble())
                    is Float -> instanceJSON = JSON_Number(instance.toDouble())
                    is Double -> instanceJSON = JSON_Number(instance)

                    is Boolean -> instanceJSON = JSON_Boolean(instance)

                    is Char -> instanceJSON = JSON_String(instance.toString())
                    is String -> instanceJSON = JSON_String(instance)

                    is Enum<*> -> instanceJSON = JSON_String(instance.name)

                }

            }

        }

        return instanceJSON

    }

}