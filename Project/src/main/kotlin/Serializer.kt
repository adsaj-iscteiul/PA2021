class Serializer() : Visitor {

    var serializedValue = ""

    override fun visit(obj: JSON_Object) {

        serializedValue = ""

        serializedValue += "{" + JSON_Whitespace.Linefeed.whitespace

        var objectFields = 0

        while (objectFields < obj.objectMap.size) {

            if (objectFields == obj.objectMap.size - 1) {

                serializedValue += obj.spacing + JSON_Whitespace.HorizontalTab.whitespace + obj.objectMap.keys.elementAt(objectFields).serialize() + " : " +
                        obj.objectMap.values.elementAt(objectFields).serialize() + JSON_Whitespace.Linefeed.whitespace

            } else {

                serializedValue += obj.spacing + JSON_Whitespace.HorizontalTab.whitespace + obj.objectMap.keys.elementAt(objectFields).serialize() + " : " +
                        obj.objectMap.values.elementAt(objectFields).serialize() + "," + JSON_Whitespace.Linefeed.whitespace

            }

            objectFields++

        }

        serializedValue += obj.spacing + "}"

    }

    override fun visit(arr: JSON_Array) {

        serializedValue = ""

        serializedValue += "[" + JSON_Whitespace.Linefeed.whitespace

        var arrayFields = 0

        while (arrayFields < arr.arrayList.size) {

            if (arrayFields == arr.arrayList.size - 1) {

                serializedValue += arr.spacing + JSON_Whitespace.HorizontalTab.whitespace + arr.arrayList.elementAt(arrayFields).serialize() + JSON_Whitespace.Linefeed.whitespace

            } else {

                serializedValue += arr.spacing + JSON_Whitespace.HorizontalTab.whitespace + arr.arrayList.elementAt(arrayFields).serialize() + "," + JSON_Whitespace.Linefeed.whitespace

            }

            arrayFields++

        }

        serializedValue += arr.spacing + "]"

    }

    override fun visit(str: JSON_String) {

        serializedValue = ""

        serializedValue += "\"" + str.string + "\""

    }

    override fun visit(num: JSON_Number) {

        serializedValue = ""

        val integerPart = num.number.toInt()
        val decimalPart = num.number - integerPart

        if (decimalPart == 0.0) {

            serializedValue += integerPart

        } else {

            serializedValue += num.number

        }

    }

    override fun visit(boo: JSON_Boolean) {

        serializedValue = ""

        serializedValue += boo.boolean

    }

    override fun visit(nul: JSON_Null) {

        serializedValue = ""

        serializedValue += null

    }

}