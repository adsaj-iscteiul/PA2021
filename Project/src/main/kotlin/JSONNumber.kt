import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

class JSONNumber (n: Double) : JSONValue() {

    val number = n

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun serialize(): String {

        val serializer = Serializer()
        this.accept(serializer)

        return serializer.serializedValue

    }
    override fun serialize(serializer: Serializer): String {

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