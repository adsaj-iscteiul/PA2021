import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

class JSONObject () : JSONValue() {

    val objectMap: MutableMap<JSONString, JSONValue> = mutableMapOf()
    override var spacing = ""

    fun addToObject(key: JSONString, value: JSONValue) {
        objectMap.put(key, value)
    }

    fun removeFromObject(key: JSONString) {
        objectMap.remove(key)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    private fun adjustSpacings() {

        var objectFields = 0

        while (objectFields < this.objectMap.size) {

            var s = this.spacing + JSONWhitespace.HorizontalTab.whitespace
            for(i in 1..(this.objectMap.keys.elementAt(objectFields).string.toCharArray().size + 5)) {
                s += JSONWhitespace.Space.whitespace
            }
            this.objectMap.values.elementAt(objectFields).spacing = s + JSONWhitespace.HorizontalTab.whitespace

            objectFields++

        }

        /*var objectFields = 0

        while (objectFields < this.objectMap.size) {

            this.objectMap.values.elementAt(objectFields).spacing = this.spacing + JSONWhitespace.HorizontalTab.whitespace + JSONWhitespace.HorizontalTab.whitespace

            objectFields++

        }*/

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