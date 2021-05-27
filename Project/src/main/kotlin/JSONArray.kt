import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

class JSONArray () : JSONValue() {

    val arrayList: MutableList<JSONValue> = mutableListOf()
    override var spacing = ""

    fun addToArray(value: JSONValue) {
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

            //var s = this.spacing
            this.arrayList.elementAt(arrayFields).spacing = this.spacing + JSONWhitespace.HorizontalTab.whitespace

            arrayFields++

        }

    }

    override fun serialize(): String {

        this.spacing = ""
        adjustSpacings()

        val serializer = Serializer()
        this.accept(serializer)

        return serializer.serializedValue

    }
    override fun serialize(serializer: Serializer): String {

        adjustSpacings()

        this.accept(serializer)

        return serializer.serializedValue

    }

    override fun search(parameters: (JSONValue) -> Boolean): MutableList<JSONValue> {

        val searcher = Searcher(parameters)
        this.accept(searcher)

        return searcher.searchedValues

    }
    override fun search(searcher: Searcher): MutableList<JSONValue> {

        this.accept(searcher)

        return searcher.searchedValues

    }

    override fun visualize(tree: Tree): MutableMap<TreeItem, JSONValue> {

        val viewer = Viewer(tree)
        this.accept(viewer)

        return viewer.treeValues

    }
    override fun visualize(viewer: Viewer): MutableMap<TreeItem, JSONValue> {

        this.accept(viewer)

        return viewer.treeValues

    }

}