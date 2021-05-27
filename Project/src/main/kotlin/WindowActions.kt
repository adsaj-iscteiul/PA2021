import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

class GenerateFile(): WindowAction {

    override val name: String
        get() = "Generate JSON File"

    override fun execute(window: Window) {

        val value = window.tree.selection.first().data as JSONValue
        window.JSON_Generator.generateJSON(value)

    }

}

class PreviewFile(): WindowAction {

    override val name: String
        get() = "Preview JSON File"

    override fun execute(window: Window) {

        val selected = window.tree.selection.first()
        val selectedText = selected.text
        val selectedValue = selected.data as JSONValue

        val windowOpened = Shell()
        windowOpened.setMinimumSize(500, 500)
        windowOpened.setLocation(250,250)
        windowOpened.layout = FillLayout()

        if (selectedValue is JSONObject || selectedValue is JSONArray) {
            if (selectedText.startsWith('{') || selectedText.startsWith('[')) windowOpened.text = selectedText.substring(1, selectedText.length - 1)
            else windowOpened.text = selectedText
        } else {
            if (selectedText.contains(":")) windowOpened.text = selectedText.split(":")[0]
            else windowOpened.text = selectedValue::class.simpleName
        }

        val JSON = Text(windowOpened, SWT.READ_ONLY or SWT.BORDER or SWT.H_SCROLL or SWT.V_SCROLL)
        JSON.text = selectedValue.serialize()
        JSON.font = Font(JSON.display, "", 15, SWT.NONE)

        windowOpened.pack()
        windowOpened.open()

    }

}

class EditProperties(): WindowAction {

    override val name: String
        get() = "Edit JSON Properties"

    override fun execute(window: Window) {

        val selected = window.tree.selection.first()
        val selectedText = selected.text
        val selectedValue = selected.data as JSONValue

        val editPropertiesWindow = Shell()
        editPropertiesWindow.setMinimumSize(350, 100)
        editPropertiesWindow.setLocation(250,250)
        editPropertiesWindow.layout = GridLayout(5, true)
        editPropertiesWindow.text = "Edit Properties"

        val insertedTexts = mutableListOf<Text>()

        if (selectedValue is JSONObject) {

            for (property in selectedValue.objectMap) {

                val keyLabel = Label(editPropertiesWindow, SWT.NONE)
                keyLabel.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 2, 1)
                keyLabel.text = property.key.string

                val valueText = Text(editPropertiesWindow, SWT.BORDER)
                valueText.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 3, 1)
                valueText.message = property.value.serialize()
                valueText.text = ""
                if (property.value is JSONObject || property.value is JSONArray) valueText.enabled = false
                insertedTexts.add(valueText)

            }

        } else {

            val keyLabel = Label(editPropertiesWindow, SWT.NONE)
            keyLabel.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 2, 1)
            if (selectedText.contains(":")) {
                keyLabel.text = selectedText.split(":")[0]
            } else {
                keyLabel.text = selectedText
            }

            val valueText = Text(editPropertiesWindow, SWT.BORDER)
            valueText.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 3, 1)
            valueText.message = selectedValue.serialize()
            valueText.text = ""
            insertedTexts.add(valueText)

        }

        val editButton = Button(editPropertiesWindow, SWT.PUSH)
        editButton.layoutData = GridData(SWT.FILL, SWT.BOTTOM, true, true, 5, 1)
        editButton.text = "Edit"
        editButton.addListener(SWT.Selection) {

            var insertedText: String = ""
            var editedValue: JSONValue = selectedValue

            if (editedValue is JSONObject) {

                var i = 0
                for (objectItem in editedValue.objectMap) {
                    insertedText = insertedTexts[i].text
                    if (insertedText != "") {
                        if (insertedText == "null") editedValue.objectMap[objectItem.key] = JSONNull()
                        else if (insertedText == "true") editedValue.objectMap[objectItem.key] = JSONBoolean(true)
                        else if (insertedText == "false") editedValue.objectMap[objectItem.key] = JSONBoolean(false)
                        else if (insertedText.matches("[+-]?([0-9]*[.])?[0-9]+".toRegex())) editedValue.objectMap[objectItem.key] = JSONNumber(insertedText.toDouble())
                        else editedValue.objectMap[objectItem.key] = JSONString(insertedText)
                        editedValue.origin = editedValue::class
                    }
                    i++
                }

            } else {

                insertedText = insertedTexts[0].text
                if (insertedText != "") {
                    if (insertedText == "null") editedValue = JSONNull()
                    else if (insertedText == "true") editedValue = JSONBoolean(true)
                    else if (insertedText == "false") editedValue = JSONBoolean(false)
                    else if (insertedText.matches("[+-]?([0-9]*[.])?[0-9]+".toRegex())) editedValue = JSONNumber(insertedText.toDouble())
                    else editedValue = JSONString(insertedText)
                    editedValue.origin = editedValue::class
                }

            }

            var currentItem: TreeItem = selected
            var editing = true
            while (editing) {

                if (window.treeContent.contains(currentItem.data)) {

                    window.treeContent[window.treeContent.indexOf(currentItem.data)] = editedValue

                    editing = false

                } else {

                    var toEdit: JSONValue = selectedValue

                    if (currentItem.parentItem.data is JSONObject) {

                        toEdit = currentItem.parentItem.data as JSONObject
                        for (objectItem in toEdit.objectMap) if (objectItem.value == selectedValue) toEdit.objectMap[objectItem.key] = editedValue

                    } else if (currentItem.parentItem.data is JSONArray) {

                        toEdit = currentItem.parentItem.data as JSONArray
                        var arrayIndex = 0
                        while (arrayIndex < toEdit.arrayList.size) {
                            if (toEdit.arrayList[arrayIndex] == selectedValue) toEdit.arrayList[arrayIndex] = editedValue
                            arrayIndex++
                        }

                    }

                    editedValue = toEdit

                    currentItem = currentItem.parentItem

                }

            }

            window.refreshTree()
            //window.tree.select(window.tree.getItem(0))
            window.serializedText.text = ""
            if (!window.generateFileButton.isDisposed) {
                window.generateFileButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.generateFileButton.background = Color(225, 225, 225)
            }
            if (!window.openInWindowButton.isDisposed) {
                window.openInWindowButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.openInWindowButton.background = Color(225, 225, 225)
            }
            if (!window.editPropertiesButton.isDisposed) {
                window.editPropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.editPropertiesButton.background = Color(225, 225, 225)
            }
            if (!window.addPropertiesButton.isDisposed) {
                window.addPropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.addPropertiesButton.background = Color(225, 225, 225)
            }
            if (!window.removePropertiesButton.isDisposed) {
                window.removePropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.removePropertiesButton.background = Color(225, 225, 225)
            }

            editPropertiesWindow.close()

        }

        editPropertiesWindow.pack()
        editPropertiesWindow.open()

    }

}

class AddProperties(): WindowAction {

    override val name: String
        get() = "Add JSON Properties"

    override fun execute(window: Window) {

        val selected = window.tree.selection.first()
        //val selectedText = selected.text
        val selectedValue = selected.data as JSONValue

        val addPropertiesWindow = Shell()
        addPropertiesWindow.setMinimumSize(350, 100)
        addPropertiesWindow.setLocation(250,250)
        addPropertiesWindow.layout = GridLayout(5, true)
        addPropertiesWindow.text = "Add Properties"

        val insertedTexts = mutableListOf<Text>()
        val optionsButtons = mutableListOf<Button>()

        if (selectedValue is JSONObject) {

            val keyLabel = Label(addPropertiesWindow, SWT.NONE)
            keyLabel.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1)
            keyLabel.text = "Key:"
            val keyText = Text(addPropertiesWindow, SWT.BORDER)
            keyText.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 4, 1)
            keyText.text = ""

            val valueLabel = Label(addPropertiesWindow, SWT.NONE)
            valueLabel.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 1, 2)
            valueLabel.text = "Value:"
            val valueText = Text(addPropertiesWindow, SWT.BORDER)
            valueText.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 4, 1)
            valueText.text = ""

            insertedTexts.add(keyText)
            insertedTexts.add(valueText)

            val otherOptions = Label(addPropertiesWindow, SWT.NONE)
            otherOptions.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1)
            otherOptions.text = "Property Options:"
            val objectButton = Button(addPropertiesWindow, SWT.CHECK)
            objectButton.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1)
            objectButton.text = "Object"
            val arrayButton = Button(addPropertiesWindow, SWT.CHECK)
            arrayButton.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1)
            arrayButton.text = "Array"

            objectButton.addListener(SWT.Selection) {
                if (objectButton.selection) {
                    valueText.text = ""
                    valueText.enabled = false
                    arrayButton.selection = false
                } else {
                    valueText.enabled = true
                }
            }
            arrayButton.addListener(SWT.Selection) {
                if (arrayButton.selection) {
                    valueText.text = ""
                    valueText.enabled = false
                    objectButton.selection = false
                } else {
                    valueText.enabled = true
                }
            }

            optionsButtons.add(objectButton)
            optionsButtons.add(arrayButton)

        } else if (selectedValue is JSONArray) {

            val valueLabel = Label(addPropertiesWindow, SWT.NONE)
            valueLabel.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 1, 2)
            valueLabel.text = "Property:"
            val valueText = Text(addPropertiesWindow, SWT.BORDER)
            valueText.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 4, 1)
            valueText.text = ""

            insertedTexts.add(valueText)

            val otherOptions = Label(addPropertiesWindow, SWT.NONE)
            otherOptions.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1)
            otherOptions.text = "Property Options:"
            val objectButton = Button(addPropertiesWindow, SWT.CHECK)
            objectButton.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1)
            objectButton.text = "Object"
            val arrayButton = Button(addPropertiesWindow, SWT.CHECK)
            arrayButton.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1)
            arrayButton.text = "Array"

            objectButton.addListener(SWT.Selection) {
                if (objectButton.selection) {
                    valueText.text = ""
                    valueText.enabled = false
                    arrayButton.selection = false
                } else {
                    valueText.enabled = true
                }
            }
            arrayButton.addListener(SWT.Selection) {
                if (arrayButton.selection) {
                    valueText.text = ""
                    valueText.enabled = false
                    objectButton.selection = false
                } else {
                    valueText.enabled = true
                }
            }

            optionsButtons.add(objectButton)
            optionsButtons.add(arrayButton)

        }

        val addButton = Button(addPropertiesWindow, SWT.PUSH)
        addButton.layoutData = GridData(SWT.FILL, SWT.BOTTOM, true, true, 5, 1)
        addButton.text = "Add"

        addButton.addListener(SWT.Selection) {

            var addingValue: JSONValue = selectedValue

            if (addingValue is JSONObject) {

                val insertedKey = insertedTexts[0].text
                val insertedValue = insertedTexts[1].text
                val objectOption = optionsButtons[0]
                val arrayOption = optionsButtons[1]

                if (insertedKey != "" && objectOption.selection) {
                    val obj = JSONObject()
                    obj.origin = obj::class//JSONObject::class
                    addingValue.addToObject(JSONString(insertedKey), obj)
                }

                else if (insertedKey != "" && arrayOption.selection) {
                    val arr = JSONArray()
                    arr.origin = arr::class
                    addingValue.addToObject(JSONString(insertedKey), arr)
                }

                else if (insertedKey != "" && insertedValue != "") {

                    val key = JSONString(insertedKey)
                    key.origin = key::class

                    val value: JSONValue
                    if (insertedValue == "null") value = JSONNull()
                    else if (insertedValue == "true") value = JSONBoolean(true)
                    else if (insertedValue == "false") value = JSONBoolean(false)
                    else if (insertedValue.matches("[+-]?([0-9]*[.])?[0-9]+".toRegex())) value = JSONNumber(insertedValue.toDouble())
                    else value = JSONString(insertedValue)
                    value.origin = value::class

                    addingValue.addToObject(key, value)

                }

            } else if (addingValue is JSONArray) {

                val insertedValue = insertedTexts[0].text
                val objectOption = optionsButtons[0]
                val arrayOption = optionsButtons[1]

                if (objectOption.selection) {
                    val obj = JSONObject()
                    obj.origin = obj::class//JSONObject::class
                    addingValue.addToArray(obj)
                }

                else if (arrayOption.selection) {
                    val arr = JSONArray()
                    arr.origin = arr::class//JSONArray::class
                    addingValue.addToArray(arr)
                }

                else if (insertedValue != "") {

                    val value: JSONValue
                    if (insertedValue == "null") value = JSONNull()
                    else if (insertedValue == "true") value = JSONBoolean(true)
                    else if (insertedValue == "false") value = JSONBoolean(false)
                    else if (insertedValue.matches("[+-]?([0-9]*[.])?[0-9]+".toRegex())) value = JSONNumber(insertedValue.toDouble())
                    else value = JSONString(insertedValue)
                    value.origin = value::class

                    addingValue.addToArray(value)

                }

            }

            var currentItem: TreeItem = selected
            var adding = true
            while (adding) {

                if (window.treeContent.contains(currentItem.data)) {

                    window.treeContent[window.treeContent.indexOf(currentItem.data)] = addingValue

                    adding = false

                } else {

                    var toEditFromAdd: JSONValue = selectedValue

                    if (currentItem.parentItem.data is JSONObject) {

                        toEditFromAdd = currentItem.parentItem.data as JSONObject
                        for (objectItem in toEditFromAdd.objectMap) if (objectItem.value == selectedValue) toEditFromAdd.objectMap[objectItem.key] = addingValue

                    } else if (currentItem.parentItem.data is JSONArray) {

                        toEditFromAdd = currentItem.parentItem.data as JSONArray
                        var arrayIndex = 0
                        while (arrayIndex < toEditFromAdd.arrayList.size) {
                            if (toEditFromAdd.arrayList[arrayIndex] == selectedValue) toEditFromAdd.arrayList[arrayIndex] = addingValue
                            arrayIndex++
                        }

                    }

                    addingValue = toEditFromAdd

                    currentItem = currentItem.parentItem

                }

            }

            window.refreshTree()
            //window.tree.select(window.tree.getItem(0))
            window.serializedText.text = ""
            if (!window.generateFileButton.isDisposed) {
                window.generateFileButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.generateFileButton.background = Color(225, 225, 225)
            }
            if (!window.openInWindowButton.isDisposed) {
                window.openInWindowButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.openInWindowButton.background = Color(225, 225, 225)
            }
            if (!window.editPropertiesButton.isDisposed) {
                window.editPropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.editPropertiesButton.background = Color(225, 225, 225)
            }
            if (!window.addPropertiesButton.isDisposed) {
                window.addPropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.addPropertiesButton.background = Color(225, 225, 225)
            }
            if (!window.removePropertiesButton.isDisposed) {
                window.removePropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.removePropertiesButton.background = Color(225, 225, 225)
            }

            addPropertiesWindow.close()

        }

        addPropertiesWindow.pack()
        addPropertiesWindow.open()

    }

}

class RemoveProperties(): WindowAction {

    override val name: String
        get() = "Remove JSON Properties"

    override fun execute(window: Window) {

        val selected = window.tree.selection.first()
        //val selectedText = selected.text
        val selectedValue = selected.data as JSONValue

        val removePropertiesWindow = Shell()
        removePropertiesWindow.setMinimumSize(350, 100)
        removePropertiesWindow.setLocation(250,250)
        removePropertiesWindow.layout = GridLayout(5, true)
        removePropertiesWindow.text = "Remove Properties"

        val checkedButtons = mutableListOf<Button>()

        if (selectedValue is JSONObject) {

            for (property in selectedValue.objectMap) {

                val propertyLabel = Label(removePropertiesWindow, SWT.NONE)
                propertyLabel.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 3, 1)
                propertyLabel.text = property.key.string

                val checkButton = Button(removePropertiesWindow, SWT.CHECK)
                checkButton.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1)
                checkedButtons.add(checkButton)

            }

        } else if (selectedValue is JSONArray) {

            for (property in selectedValue.arrayList) {

                val propertyLabel = Label(removePropertiesWindow, SWT.NONE)
                propertyLabel.layoutData = GridData(SWT.FILL, SWT.FILL, true, false, 3, 1)
                if (property is JSONObject) propertyLabel.text = property.origin.simpleName
                else if (property is JSONArray) propertyLabel.text = property.origin.simpleName
                else if (property is JSONString) propertyLabel.text = property.string
                else if (property is JSONNumber) propertyLabel.text = property.number.toString()
                else if (property is JSONBoolean) propertyLabel.text = property.boolean.toString()
                else if (property is JSONNull) propertyLabel.text = "null"

                val checkButton = Button(removePropertiesWindow, SWT.CHECK)
                checkButton.layoutData = GridData(SWT.CENTER, SWT.FILL, true, false, 2, 1)
                checkedButtons.add(checkButton)

            }

        }

        val removeButton = Button(removePropertiesWindow, SWT.PUSH)
        removeButton.layoutData = GridData(SWT.FILL, SWT.BOTTOM, true, true, 5, 1)
        removeButton.text = "Remove"
        removeButton.addListener(SWT.Selection) {

            var removingValue: JSONValue = selectedValue

            var i = 0
            if (removingValue is JSONObject) {

                val removingFromObject = mutableListOf<JSONString>()
                for (property in removingValue.objectMap) {
                    if (checkedButtons[i].selection) removingFromObject.add(property.key) //removedValue.removeFromObject(property.key)
                    i++
                }
                for (r in removingFromObject) removingValue.removeFromObject(r)

            } else if (removingValue is JSONArray) {

                val removingFromArray = mutableListOf<Int>()
                for (property in removingValue.arrayList) {
                    if (checkedButtons[i].selection) removingFromArray.add(0, i) //removedValue.removeFromArray(i)
                    i++
                }
                for (r in removingFromArray) removingValue.removeFromArray(r)

            }

            var currentItem: TreeItem = selected
            var removing = true
            while (removing) {

                if (window.treeContent.contains(currentItem.data)) {

                    window.treeContent[window.treeContent.indexOf(currentItem.data)] = removingValue

                    removing = false

                } else {

                    var toEditFromRemove: JSONValue = selectedValue

                    if (currentItem.parentItem.data is JSONObject) {

                        toEditFromRemove = currentItem.parentItem.data as JSONObject
                        for (objectItem in toEditFromRemove.objectMap) if (objectItem.value == selectedValue) toEditFromRemove.objectMap[objectItem.key] = removingValue

                    } else if (currentItem.parentItem.data is JSONArray) {

                        toEditFromRemove = currentItem.parentItem.data as JSONArray
                        var arrayIndex = 0
                        while (arrayIndex < toEditFromRemove.arrayList.size) {
                            if (toEditFromRemove.arrayList[arrayIndex] == selectedValue) toEditFromRemove.arrayList[arrayIndex] = removingValue
                            arrayIndex++
                        }

                    }

                    removingValue = toEditFromRemove

                    currentItem = currentItem.parentItem

                }

            }

            window.refreshTree()
            //window.tree.select(window.tree.getItem(0))
            window.serializedText.text = ""
            if (!window.generateFileButton.isDisposed) {
                window.generateFileButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.generateFileButton.background = Color(225, 225, 225)
            }
            if (!window.openInWindowButton.isDisposed) {
                window.openInWindowButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.openInWindowButton.background = Color(225, 225, 225)
            }
            if (!window.editPropertiesButton.isDisposed) {
                window.editPropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.editPropertiesButton.background = Color(225, 225, 225)
            }
            if (!window.addPropertiesButton.isDisposed) {
                window.addPropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.addPropertiesButton.background = Color(225, 225, 225)
            }
            if (!window.removePropertiesButton.isDisposed) {
                window.removePropertiesButton.enabled = false
                if (window.treeState == 2 || window.treeState == 3) window.removePropertiesButton.background = Color(225, 225, 225)
            }

            removePropertiesWindow.close()

        }

        removePropertiesWindow.pack()
        removePropertiesWindow.open()

    }

}