class Searcher(val parameters: (JSONValue) -> Boolean) : Visitor {

    var searchedValues = mutableListOf<JSONValue>()

    override fun visit(obj: JSONObject) {

        //searchedValues.clear()

        if (parameters(obj)) {
            searchedValues.add(obj)
        }

        var objectFields = 0

        while (objectFields < obj.objectMap.size) {

            //searchedValues.addAll(obj.objectMap.keys.elementAt(objectFields).search(parameters))
            //searchedValues.addAll(obj.objectMap.values.elementAt(objectFields).search(this))
            obj.objectMap.values.elementAt(objectFields).search(this)

            objectFields++

        }

    }

    override fun visit(arr: JSONArray) {

        //searchedValues.clear()

        if (parameters(arr)) {
            searchedValues.add(arr)
        }

        var arrayFields = 0

        while (arrayFields < arr.arrayList.size) {

            //searchedValues.addAll(arr.arrayList.elementAt(arrayFields).search(this))
            arr.arrayList.elementAt(arrayFields).search(this)

            arrayFields++

        }

    }

    override fun visit(str: JSONString) {

        //searchedValues.clear()

        if (parameters(str)) {
            searchedValues.add(str)
        }

    }

    override fun visit(num: JSONNumber) {

        //searchedValues.clear()

        if (parameters(num)) {
            searchedValues.add(num)
        }

    }

    override fun visit(boo: JSONBoolean) {

        //searchedValues.clear()

        if (parameters(boo)) {
            searchedValues.add(boo)
        }

    }

    override fun visit(nul: JSONNull) {

        //searchedValues.clear()

        if (parameters(nul)) {
            searchedValues.add(nul)
        }

    }

}