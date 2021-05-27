import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Display

class DefaultSetup(): WindowSetup {

    override val name: String
        get() = "Default"

    override fun execute(window: Window) {
        window.treeState = 1
        window.iconsPathFolder = name
    }

}

class DetailedSetup(): WindowSetup {

    override val name: String
        get() = "Detailed"

    override fun execute(window: Window) {
        window.treeState = 2
        window.iconsPathFolder = name
        window.generateFileButton.background = Color(250, 250, 250)
        window.openInWindowButton.background = Color(250, 250, 250)
        window.buttons.background = Color(200,200,200)
    }

}

class CleanSetup(): WindowSetup {

    override val name: String
        get() = "Clean"

    override fun execute(window: Window) {
        window.treeState = 3
        window.iconsPathFolder = name
        window.generateFileButton.background = Color(250, 250, 250)
        window.openInWindowButton.background = Color(250, 250, 250)
        window.buttons.background = Color(200,200,200)
    }

}