import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

class Viewer(val tree: Tree) : Visitor {

    var treeValues = mutableMapOf<TreeItem, JSONValue>()

    private var treeDepth = 0
    lateinit var parent: TreeItem

    private var inObject = false
    private var hasKey = false
    private var keyString = ""

    override fun visit(obj: JSONObject) {

        val treeObject: TreeItem

        if (treeDepth == 0) treeObject = TreeItem(tree, SWT.NONE)
        else treeObject = TreeItem(parent, SWT.NONE)

        if (inObject) treeObject.text = "{" + keyString + "}"
        else treeObject.text = "{" + obj.origin.simpleName + "}" //"{ Object }"

        treeObject.data = obj
        parent = treeObject
        treeDepth++

        var objectFields = 0

        while (objectFields < obj.objectMap.size) {

            inObject = true
            hasKey = true

            keyString = obj.objectMap.keys.elementAt(objectFields).string
            obj.objectMap.values.elementAt(objectFields).visualize(this)
            parent = treeObject

            objectFields++

        }

        inObject = false

        treeValues.put(treeObject, obj)

    }

    override fun visit(arr: JSONArray) {

        val treeArray: TreeItem

        if (treeDepth == 0) treeArray = TreeItem(tree, SWT.NONE)
        else treeArray = TreeItem(parent, SWT.NONE)

        if (inObject) treeArray.text = "[" + keyString + "]"
        else treeArray.text = "[" + arr.origin.simpleName + "]" //"[ Array ]"

        treeArray.data = arr
        parent = treeArray
        treeDepth++

        inObject = false
        hasKey = false

        var arrayFields = 0

        while (arrayFields < arr.arrayList.size) {

            arr.arrayList.elementAt(arrayFields).visualize(this)
            parent = treeArray

            arrayFields++

        }

        treeValues.put(treeArray, arr)

    }

    override fun visit(str: JSONString) {

        val treeString: TreeItem

        if (treeDepth == 0) treeString = TreeItem(tree, SWT.NONE)
        else treeString = TreeItem(parent, SWT.NONE)

        if (hasKey) treeString.text = keyString + ": " + str.serialize()
        else treeString.text = str.serialize()

        hasKey = false

        treeString.data = str
        parent = treeString
        treeDepth++

        treeValues.put(treeString, str)

    }

    override fun visit(num: JSONNumber) {

        val treeNumber: TreeItem

        if (treeDepth == 0) treeNumber = TreeItem(tree, SWT.NONE)
        else treeNumber = TreeItem(parent, SWT.NONE)

        if (hasKey) treeNumber.text = keyString + ": " + num.serialize()
        else treeNumber.text = num.serialize()

        hasKey = false

        treeNumber.data = num
        parent = treeNumber
        treeDepth++

        treeValues.put(treeNumber, num)

    }

    override fun visit(boo: JSONBoolean) {

        val treeBoolean: TreeItem

        if (treeDepth == 0) treeBoolean = TreeItem(tree, SWT.NONE)
        else treeBoolean = TreeItem(parent, SWT.NONE)

        if (hasKey) treeBoolean.text = keyString + ": " + boo.serialize()
        else treeBoolean.text = boo.serialize()

        hasKey = false

        treeBoolean.data = boo
        parent = treeBoolean
        treeDepth++

        treeValues.put(treeBoolean, boo)

    }

    override fun visit(nul: JSONNull) {

        val treeNull: TreeItem

        if (treeDepth == 0) treeNull = TreeItem(tree, SWT.NONE)
        else treeNull = TreeItem(parent, SWT.NONE)

        if (hasKey) treeNull.text = keyString + ": null"
        else treeNull.text = "null"

        hasKey = false

        treeNull.data = nul
        parent = treeNull
        treeDepth++

        treeValues.put(treeNull, nul)

    }

}