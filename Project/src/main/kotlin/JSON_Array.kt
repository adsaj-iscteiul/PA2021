class JSON_Array () : JSON_Value() {

    val arrayList: MutableList<JSON_Value> = mutableListOf()
    override var spacing = ""

    fun addToArray(value: JSON_Value) {
        arrayList.add(value)
    }

    fun removeFromArray(position: Int) {
        arrayList.removeAt(position)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    private fun adjustSpacings() {

        var arrayFields = 0

        while (arrayFields < this.arrayList.size) {

            var s = this.spacing
            this.arrayList.elementAt(arrayFields).spacing = s

            arrayFields++

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