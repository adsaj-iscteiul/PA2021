import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class Generator() {

    val JSON_Files: MutableMap<File, JSONValue> = mutableMapOf()
    var pathToFiles: String = ""
    var generatedFiles: Int = 0

    init {

        var existingDirectory = true
        var freeDirectory = 1
        while (existingDirectory) {

            val path = Paths.get("").toAbsolutePath().toString() + "\\JSON_Files\\Generation" + freeDirectory
            val directory = File(path)

            if (directory.isDirectory && directory.exists()) {
                freeDirectory++
            } else {
                existingDirectory = false
                pathToFiles = path
                directory.mkdir()
            }

        }

    }

    private fun checkFiles() {

        val missingFiles = mutableListOf<File>()

        for (file in JSON_Files) {

            if (!file.key.exists()) {

                missingFiles.add(file.key)

            }

        }

        for (missingFile in missingFiles) {

            JSON_Files.remove(missingFile)

        }

        if (missingFiles.size > 0) renameFiles()

    }

    private fun renameFiles() {

        val renamedFiles: MutableMap<File, JSONValue> = mutableMapOf()

        var arrays = 0
        var booleans = 0
        var nulls = 0
        var numbers = 0
        var objects = 0
        var strings = 0

        for (file in JSON_Files) {

            var renamedFile: File = file.key
            //file.key.renameTo()

            when(file.value) {

                is JSONArray -> {
                    arrays++
                    val fileName = file.key.name.split("_")
                    if (!fileName[0].endsWith(arrays.toString())) renamedFile = File(pathToFiles, "JSONArray" + arrays + "_" + fileName[1])
                    //else renamedFile = file.key
                }

                is JSONBoolean -> {
                    booleans++
                    val fileName = file.key.name.split("_")
                    if (!fileName[0].endsWith(booleans.toString())) renamedFile = File(pathToFiles, "JSONBoolean" + booleans + "_" + fileName[1])
                    //else renamedFile = file.key
                }

                is JSONNull -> {
                    nulls++
                    val fileName = file.key.name
                    if (!fileName.endsWith(nulls.toString())) renamedFile = File(pathToFiles, "JSONNull" + nulls + ".json")
                    //else renamedFile = file.key
                }

                is JSONNumber -> {
                    numbers++
                    val fileName = file.key.name.split("_")
                    if (!fileName[0].endsWith(numbers.toString())) renamedFile = File(pathToFiles, "JSONNumber" + numbers + "_" + fileName[1])
                    //else renamedFile = file.key
                }

                is JSONObject -> {
                    objects++
                    val fileName = file.key.name.split("_")
                    if (!fileName[0].endsWith(objects.toString())) renamedFile = File(pathToFiles, "JSONObject" + objects + "_" + fileName[1])
                    //else renamedFile = file.key
                }

                is JSONString -> {
                    strings++
                    val fileName = file.key.name.split("_")
                    if (!fileName[0].endsWith(strings.toString())) renamedFile = File(pathToFiles, "JSONString" + strings + "_" + fileName[1])
                    //else renamedFile = file.key
                }

            }

            renamedFiles.put(renamedFile, file.value)

        }

        for (removingFile in JSON_Files) {

            removingFile.key.delete()
            JSON_Files.remove(removingFile.key)

        }

        for (file in renamedFiles) {

            file.key.createNewFile()
            file.key.appendText(file.value.serialize())
            JSON_Files.put(file.key, file.value)

        }

    }

    fun generateJSON(instance: Any?) {

        checkFiles()

        val fileValue: JSONValue

        if (instance == null) fileValue = JSONNull()
        else if (instance is JSONValue) fileValue = instance
        else fileValue = toJSON(instance)

        val fileContent = fileValue.serialize()

        var existingFile = false
        var filesCounter = 0

        for (file in JSON_Files) {

            if (fileValue::class == file.value::class) {

                if (fileContent.equals(file.value.serialize())) existingFile = true

                filesCounter++

            }

        }

        if (!existingFile) {

            val fileName: String

            if (fileValue is JSONNull) fileName = fileValue::class.simpleName + (filesCounter + 1)
            else fileName = fileValue::class.simpleName + (filesCounter + 1) + "_" + fileValue.origin.simpleName

            val file = File(pathToFiles, fileName + ".json")

            //file.delete()
            file.createNewFile()
            file.appendText(fileContent)

            JSON_Files.put(file, fileValue)

        }

    }

    fun toJSON(instance: Any?): JSONValue {

        var instanceJSON: JSONValue = JSONNull()

        if (instance != null) {

            if (instance::class.isData) {

                instanceJSON = JSONObject()

                val instanceClass: KClass<*> = instance::class //as KClass<*>

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
                        (instanceJSON as JSONObject).addToObject(key as JSONString, value)

                    }

                }

            } else {

                when (instance) {

                    is Collection<*> -> {

                        instanceJSON = JSONArray()

                        var collectionFields = 0
                        while (collectionFields < instance.size) {

                            val fieldElement = toJSON(instance.elementAt(collectionFields))
                            instanceJSON.addToArray(fieldElement)

                            collectionFields++

                        }

                    }

                    is Map<*,*> -> {

                        instanceJSON = JSONObject()

                        var mapFields = 0
                        while (mapFields < instance.size) {

                            val fieldKey = toJSON(instance.keys.elementAt(mapFields).toString())
                            val fieldValue = toJSON(instance.values.elementAt(mapFields))
                            instanceJSON.addToObject(fieldKey as JSONString, fieldValue)

                            mapFields++

                        }

                    }

                    is Byte -> instanceJSON = JSONNumber(instance.toDouble())
                    is Short -> instanceJSON = JSONNumber(instance.toDouble())
                    is Int -> instanceJSON = JSONNumber(instance.toDouble())
                    is Long -> instanceJSON = JSONNumber(instance.toDouble())
                    is Float -> instanceJSON = JSONNumber(instance.toDouble())
                    is Double -> instanceJSON = JSONNumber(instance)

                    is Boolean -> instanceJSON = JSONBoolean(instance)

                    is Char -> instanceJSON = JSONString(instance.toString())
                    is String -> instanceJSON = JSONString(instance)

                    is Enum<*> -> instanceJSON = JSONString(instance.name)

                }

            }

            instanceJSON.origin = instance::class

        }

        return instanceJSON

    }

}