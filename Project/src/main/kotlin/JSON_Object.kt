class JSON_Object () : JSON_Value() {

    val objectMap: MutableMap<JSON_String, JSON_Value> = mutableMapOf()
    override var spacing = ""

    fun addToObject(string: JSON_String, value: JSON_Value) {
        objectMap.put(string, value)
    }

    fun removeFromObject(key: JSON_String) {
        objectMap.remove(key)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    private fun adjustSpacings() {

        var objectFields = 0

        while (objectFields < this.objectMap.size) {

            var s = this.spacing + JSON_Whitespace.HorizontalTab.whitespace
            for(i in 1..(this.objectMap.keys.elementAt(objectFields).string.toCharArray().size + 5)) {
                s += JSON_Whitespace.Space.whitespace
            }
            this.objectMap.values.elementAt(objectFields).spacing = s

            objectFields++

        }

    }

    override fun serialize(): String {

        adjustSpacings()

        val serializer = Serializer()
        this.accept(serializer)

        return serializer.serializedValue

    }

    override fun search(parameters: (JSON_Value) -> Boolean): MutableList<JSON_Value> {

        val searcher = Searcher(parameters)
        this.accept(searcher)

        return searcher.searchedValues

    }

}