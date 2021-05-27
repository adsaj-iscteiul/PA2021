import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import java.nio.file.Paths

class Window {

    val shell: Shell = Shell(Display.getDefault())

    val tree: Tree = Tree(shell, SWT.SINGLE or SWT.BORDER)
    val serializedText: Text = Text(shell, SWT.READ_ONLY or SWT.BORDER or SWT.H_SCROLL or SWT.V_SCROLL)
    val searchText: Text = Text(shell, SWT.BORDER)

    val buttons: Composite = Composite(shell, SWT.BORDER)
    var generateFileButton: Button = Button(buttons, SWT.PUSH)
    var openInWindowButton: Button = Button(buttons, SWT.PUSH)
    var editPropertiesButton: Button = Button(buttons, SWT.PUSH)
    var addPropertiesButton: Button = Button(buttons, SWT.PUSH)
    var removePropertiesButton: Button = Button(buttons, SWT.PUSH)

    var treeContent: MutableList<JSONValue> = mutableListOf()
    var treeMapping: MutableMap<TreeItem, JSONValue> = mutableMapOf()

    val JSON_Generator: Generator = Generator()

    var treeState: Int = 0
    var iconsPathFolder = ""

    @Injector.InjectSetup
    lateinit var setup: WindowSetup
    @Injector.InjectActions
    lateinit var actions: MutableList<WindowAction>
    //val actions: MutableList<WindowAction> = mutableListOf()

    init {

        shell.setMinimumSize(1000, 500)
        shell.setMaximumSize(1500, 750)
        shell.setLocation(50,50)
        shell.text = "JSON Tree"
        shell.layout = GridLayout(5, true)

        tree.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 2, 1)

        serializedText.text = ""
        serializedText.layoutData = GridData(SWT.FILL, SWT.FILL, true, true, 3, 2)

        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {

                val value = tree.selection.first().data as JSONValue
                serializedText.text = value.serialize()

                if (value is JSONObject) {
                    if (!editPropertiesButton.isDisposed) {
                        editPropertiesButton.enabled = true
                        if (treeState == 2) editPropertiesButton.background = Color(250, 250, 250)
                        else if (treeState == 3) editPropertiesButton.background = Color(225, 200, 150)
                    }
                    if (!addPropertiesButton.isDisposed) {
                        addPropertiesButton.enabled = true
                        if (treeState == 2) addPropertiesButton.background = Color(250, 250, 250)
                        else if (treeState == 3) addPropertiesButton.background = Color(225, 200, 150)
                    }
                    if (!removePropertiesButton.isDisposed) {
                        removePropertiesButton.enabled = true
                        if (treeState == 2) removePropertiesButton.background = Color(250, 250, 250)
                        else if (treeState == 3) removePropertiesButton.background = Color(225, 200, 150)
                    }
                } else if (value is JSONArray) {
                    if (!editPropertiesButton.isDisposed) {
                        editPropertiesButton.enabled = false
                        if (treeState == 2 || treeState == 3) editPropertiesButton.background = Color(225, 225, 225)
                        //else if (treeState == 3) editPropertiesButton.background = Color(225, 225, 225)
                    }
                    if (!addPropertiesButton.isDisposed) {
                        addPropertiesButton.enabled = true
                        if (treeState == 2) addPropertiesButton.background = Color(250, 250, 250)
                        else if (treeState == 3) addPropertiesButton.background = Color(125, 175, 200)
                    }
                    if (!removePropertiesButton.isDisposed) {
                        removePropertiesButton.enabled = true
                        if (treeState == 2) removePropertiesButton.background = Color(250, 250, 250)
                        else if (treeState == 3) removePropertiesButton.background = Color(125, 175, 200)
                    }
                } else {
                    if (!editPropertiesButton.isDisposed) {
                        editPropertiesButton.enabled = true
                        if (treeState == 2 || treeState == 3) editPropertiesButton.background = Color(250, 250, 250)
                        //else if (treeState == 3) editPropertiesButton.background = Color(250, 250, 250)
                    }
                    if (!addPropertiesButton.isDisposed) {
                        addPropertiesButton.enabled = false
                        if (treeState == 2 || treeState == 3) addPropertiesButton.background = Color(225, 225, 225)
                    }
                    if (!removePropertiesButton.isDisposed) {
                        removePropertiesButton.enabled = false
                        if (treeState == 2 || treeState == 3) removePropertiesButton.background = Color(225, 225, 225)
                    }
                }

                if (!generateFileButton.isDisposed && !generateFileButton.enabled) {
                    generateFileButton.enabled = true
                    if (treeState == 2 || treeState == 3) generateFileButton.background = Color(250, 250, 250)
                }
                if (!openInWindowButton.isDisposed && !openInWindowButton.enabled) {
                    openInWindowButton.enabled = true
                    if (treeState == 2 || treeState == 3) openInWindowButton.background = Color(250, 250, 250)
                }

            }
        })

        searchText.message = "Search"
        searchText.layoutData = GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1)
        searchText.addListener(SWT.Modify) {

            val searchParameters: (JSONValue) -> Boolean =
                { value ->
                    (value is JSONNull && "null".startsWith(searchText.text))
                            || (value is JSONBoolean && value.boolean && "true".startsWith(searchText.text))
                            || (value is JSONBoolean && !value.boolean && "false".startsWith(searchText.text))
                            || (value is JSONNumber && searchText.text.matches("[+-]?([0-9]*[.])?[0-9]+".toRegex()) && value.number.toString().startsWith(searchText.text))
                            || (value is JSONString && value.string.startsWith(searchText.text))
                            //|| (value is JSONArray && value.origin.simpleName == searchText.text)
                            //|| (value is JSONObject && value.origin.simpleName == searchText.text)
                }

            val searchResults = mutableListOf<JSONValue>()
            for (value in treeContent) searchResults.addAll(value.search(searchParameters))

            for (item in treeMapping) {
                if (searchText.text != "") {
                    if (searchResults.contains(item.value)) item.key.background = Color(100, 200, 200)
                    else item.key.background = Color(255, 255, 255)
                } else {
                    item.key.background = Color(255, 255, 255)
                }
            }

            //for (treeItem in tree.items) {
            //    treeSearch(treeItem, searchText.text)
            //}

        }

        buttons.layoutData = GridData(SWT.FILL, SWT.BOTTOM, true, false, 5, 1)

    }

    fun TreeItem.depth(): Int =
        if (parentItem == null) 0
        else 1 + parentItem.depth()

/*
    fun treeSearch(treeItem: TreeItem, searchedText: String) {

        if(treeItem.text.contains(searchedText) && searchedText != "") {
            treeItem.setBackground(Color(100, 200, 200))
        } else {
            treeItem.setBackground(Color(255, 255, 255))
        }

        treeItem.items.forEach { treeSearch(it, searchedText) }

    }
*/

    fun insertTreeIcons(treeItem: TreeItem) {

        val path = Paths.get("").toAbsolutePath().toString() + "\\Icons\\" + iconsPathFolder + "\\"

        if (treeState == 2) {

            if (treeItem.data is JSONObject && (treeItem.data as JSONObject).objectMap.isEmpty()) treeItem.image = Image(Display.getDefault(), path + "object_empty.ico")
            else if (treeItem.data is JSONObject && (treeItem.data as JSONObject).objectMap.isNotEmpty()) treeItem.image = Image(Display.getDefault(), path + "object_filled.ico")
            else if (treeItem.data is JSONArray && (treeItem.data as JSONArray).arrayList.isEmpty()) treeItem.image = Image(Display.getDefault(), path + "array_empty.ico")
            else if (treeItem.data is JSONArray && (treeItem.data as JSONArray).arrayList.isNotEmpty()) treeItem.image = Image(Display.getDefault(), path + "array_filled.ico")
            else if (treeItem.data is JSONString) treeItem.image = Image(Display.getDefault(), path + "string.ico")
            else if (treeItem.data is JSONNumber) treeItem.image = Image(Display.getDefault(), path + "number.ico")
            else if (treeItem.data is JSONBoolean && (treeItem.data as JSONBoolean).boolean) treeItem.image = Image(Display.getDefault(), path + "true.ico")
            else if (treeItem.data is JSONBoolean && !(treeItem.data as JSONBoolean).boolean) treeItem.image = Image(Display.getDefault(), path + "false.ico")
            else if (treeItem.data is JSONNull) treeItem.image = Image(Display.getDefault(), path + "null.ico")

        } else if (treeState == 3) {

            if (treeItem.data is JSONObject && (treeItem.data as JSONObject).objectMap.isEmpty()) treeItem.image = Image(Display.getDefault(), path + "object_empty.ico")
            else if (treeItem.data is JSONObject && (treeItem.data as JSONObject).objectMap.isNotEmpty()) treeItem.image = Image(Display.getDefault(), path + "object_filled.ico")
            else if (treeItem.data is JSONArray && (treeItem.data as JSONArray).arrayList.isEmpty()) treeItem.image = Image(Display.getDefault(), path + "array_empty.ico")
            else if (treeItem.data is JSONArray && (treeItem.data as JSONArray).arrayList.isNotEmpty()) treeItem.image = Image(Display.getDefault(), path + "array_filled.ico")
            else if (treeItem.data is JSONNull) treeItem.image = Image(Display.getDefault(), path + "null.ico")
            else treeItem.image = Image(Display.getDefault(), path + "not_null.ico")

            if (treeItem.text.contains(":")) treeItem.text = treeItem.text.split(":")[0]
            else if (treeItem.text.startsWith('{') && treeItem.text.endsWith('}')) treeItem.text = treeItem.text.substring(1, treeItem.text.length - 1)
            else if (treeItem.text.startsWith('[') && treeItem.text.endsWith(']')) treeItem.text = treeItem.text.substring(1, treeItem.text.length - 1)

        }

        treeItem.items.forEach { insertTreeIcons(it) }

    }

    fun showTree(list: MutableList<Any?>) {

        for (item in list) {

            var value: JSONValue

            if (item is JSONValue) {

                value = item

            } else {

                value = JSON_Generator.toJSON(item)

            }

            treeContent.add(value)
            treeMapping.putAll(value.visualize(tree))

        }

        if (treeState > 1) tree.items.forEach { insertTreeIcons(it) }

        tree.expandAll()

    }

    fun refreshTree() {

        tree.removeAll()
        treeMapping.clear()

        for (item in treeContent) treeMapping.putAll(item.visualize(tree))

        if (treeState > 1) tree.items.forEach { insertTreeIcons(it) }

        tree.expandAll()

    }

    fun open(list: MutableList<Any?>) {

        setup.execute(this)

        buttons.layout = GridLayout(actions.size, true)
        actions.forEach {

            var actionButton: Button? = null
            var validButton = true

            if (it.name == "Generate JSON File") actionButton = generateFileButton
            else if (it.name == "Preview JSON File") actionButton = openInWindowButton
            else if (it.name == "Edit JSON Properties") actionButton = editPropertiesButton
            else if (it.name == "Add JSON Properties") actionButton = addPropertiesButton
            else if (it.name == "Remove JSON Properties") actionButton = removePropertiesButton
            else validButton = false

            if (validButton) {
                actionButton!!.text = it.name
                actionButton.layoutData = GridData(SWT.FILL, SWT.BOTTOM, true, false)
                actionButton.addListener(SWT.Selection) { event -> it.execute(this) }
            }

        }
        buttons.children.forEach { if ((it as Button).text == "") it.dispose() }

        showTree(list)

        shell.pack()
        shell.open()

        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()

    }

    // auxiliares para varrer a árvore

    fun Tree.expandAll() = traverse { it.expanded = true }

    fun Tree.traverse(visitor: (TreeItem) -> Unit) {
        fun TreeItem.traverse() {
            visitor(this)
            items.forEach {
                it.traverse()
            }
        }
        items.forEach { it.traverse() }
    }

}