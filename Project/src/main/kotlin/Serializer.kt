class Serializer() : Visitor {

    var serializedValue = ""

    override fun visit(obj: JSONObject) {

        //serializedValue = ""

        serializedValue += obj.spacing + "{" + JSONWhitespace.Linefeed.whitespace

        var objectFields = 0

        while (objectFields < obj.objectMap.size) {

            val currentKey = obj.objectMap.keys.elementAt(objectFields)
            val currentValue = obj.objectMap.values.elementAt(objectFields)

            if (currentValue is JSONObject || currentValue is JSONArray) {

                //serializedValue += obj.spacing + JSONWhitespace.HorizontalTab.whitespace + currentKey.serialize(this) + JSONWhitespace.Space.whitespace +
                        //":" + JSONWhitespace.Linefeed.whitespace + currentValue.serialize(this)
                serializedValue += obj.spacing + JSONWhitespace.HorizontalTab.whitespace
                currentKey.serialize(this)
                serializedValue += JSONWhitespace.Space.whitespace + ":" + JSONWhitespace.Space.whitespace + JSONWhitespace.Linefeed.whitespace
                currentValue.serialize(this)

            } else {

                //serializedValue += obj.spacing + JSONWhitespace.HorizontalTab.whitespace + currentKey.serialize(this) + JSONWhitespace.Space.whitespace +
                        //":" + JSONWhitespace.Space.whitespace + currentValue.serialize(this)
                serializedValue += obj.spacing + JSONWhitespace.HorizontalTab.whitespace
                currentKey.serialize(this)
                serializedValue += JSONWhitespace.Space.whitespace + ":" + JSONWhitespace.Space.whitespace
                currentValue.serialize(this)

            }

            if (objectFields != obj.objectMap.size - 1) {
                serializedValue += ","
            }
            serializedValue += JSONWhitespace.Linefeed.whitespace

            objectFields++

        }

        serializedValue += obj.spacing + "}"

    }

    override fun visit(arr: JSONArray) {

        //serializedValue = ""

        serializedValue += arr.spacing + "[" + JSONWhitespace.Linefeed.whitespace

        var arrayFields = 0

        while (arrayFields < arr.arrayList.size) {

            val currentElement = arr.arrayList.elementAt(arrayFields)

            if (currentElement is JSONObject || currentElement is JSONArray) {

                //serializedValue += currentElement.serialize(this)
                currentElement.serialize(this)

            } else {

                //serializedValue += arr.spacing + JSONWhitespace.HorizontalTab.whitespace + currentElement.serialize(this)
                serializedValue += arr.spacing + JSONWhitespace.HorizontalTab.whitespace
                currentElement.serialize(this)

            }

            if (arrayFields != arr.arrayList.size - 1) {
                serializedValue += ","
            }
            serializedValue += JSONWhitespace.Linefeed.whitespace

            arrayFields++

        }

        serializedValue += arr.spacing + "]"

    }

    override fun visit(str: JSONString) {

        //serializedValue = ""

        serializedValue += "\"" + str.string + "\""

    }

    override fun visit(num: JSONNumber) {

        //serializedValue = ""

        val integerPart = num.number.toInt()
        val decimalPart = num.number - integerPart

        if (decimalPart == 0.0) {

            serializedValue += integerPart

        } else {

            serializedValue += num.number

        }

    }

    override fun visit(boo: JSONBoolean) {

        //serializedValue = ""

        serializedValue += boo.boolean

    }

    override fun visit(nul: JSONNull) {

        //serializedValue = ""

        serializedValue += null

    }

}