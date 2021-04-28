class JSON_Null() : JSON_Value() {

    // val n = null

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun serialize(): String {

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