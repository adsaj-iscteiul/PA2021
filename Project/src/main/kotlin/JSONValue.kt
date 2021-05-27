import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem
import kotlin.reflect.KClass

abstract class JSONValue () {

    open lateinit var origin: KClass<*>

    open var spacing: String = ""
        get() = field
        set(value) { field = value }

    abstract fun accept(visitor: Visitor)

    abstract fun serialize(): String
    abstract fun serialize(serializer: Serializer): String

    abstract fun search(parameters: (JSONValue) -> Boolean): MutableList<JSONValue>
    abstract fun search(searcher: Searcher): MutableList<JSONValue>

    abstract fun visualize(tree: Tree): MutableMap<TreeItem, JSONValue>
    abstract fun visualize(viewer: Viewer): MutableMap<TreeItem, JSONValue>

}