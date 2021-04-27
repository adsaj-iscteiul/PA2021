abstract class JSON_Value () {

    open var spacing: String = ""
        get() = field
        set(value) { field = value }

    abstract fun accept(visitor: Visitor)

    abstract fun serialize(): String
    abstract fun search(parameters: (JSON_Value) -> Boolean): MutableList<JSON_Value>

}