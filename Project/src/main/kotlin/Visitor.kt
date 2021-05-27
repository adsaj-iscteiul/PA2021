interface Visitor {

    fun visit(obj: JSONObject)
    fun visit(arr: JSONArray)
    fun visit(str: JSONString)
    fun visit(num: JSONNumber)
    fun visit(boo: JSONBoolean)
    fun visit(nul: JSONNull)

}