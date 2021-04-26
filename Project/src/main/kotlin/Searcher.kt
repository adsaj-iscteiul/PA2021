class Searcher(val parameters: (JSON_Value) -> Boolean) : Visitor {

    var searchedValues = mutableListOf<JSON_Value>()

    override fun visit(obj: JSON_Object) {

        searchedValues.clear()

        if (parameters(obj)) {
            searchedValues.add(obj)
        }

        var objectFields = 0

        while (objectFields < obj.objectMap.size) {

            searchedValues.addAll(obj.objectMap.keys.elementAt(objectFields).search(parameters))
            searchedValues.addAll(obj.objectMap.values.elementAt(objectFields).search(parameters))

            objectFields++

        }

    }

    override fun visit(arr: JSON_Array) {

        searchedValues.clear()

        if (parameters(arr)) {
            searchedValues.add(arr)
        }

        var arrayFields = 0

        while (arrayFields < arr.arrayList.size) {

            searchedValues.addAll(arr.arrayList.elementAt(arrayFields).search(parameters))

            arrayFields++

        }

    }

    override fun visit(str: JSON_String) {

        searchedValues.clear()

        if (parameters(str)) {
            searchedValues.add(str)
        }

    }

    override fun visit(num: JSON_Number) {

        searchedValues.clear()

        if (parameters(num)) {
            searchedValues.add(num)
        }

    }

    override fun visit(boo: JSON_Boolean) {

        searchedValues.clear()

        if (parameters(boo)) {
            searchedValues.add(boo)
        }

    }

    override fun visit(nul: JSON_Null) {

        searchedValues.clear()

        if (parameters(nul)) {
            searchedValues.add(nul)
        }

    }

}