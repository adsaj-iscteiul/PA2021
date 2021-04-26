interface Visitor {

    fun visit(obj: JSON_Object)
    fun visit(arr: JSON_Array)
    fun visit(str: JSON_String)
    fun visit(num: JSON_Number)
    fun visit(boo: JSON_Boolean)
    fun visit(nul: JSON_Null)

}