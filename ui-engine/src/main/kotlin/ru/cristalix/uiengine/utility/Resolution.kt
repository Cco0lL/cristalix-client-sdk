package ru.cristalix.uiengine.utility

import org.lwjgl.opengl.Display
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.ContextGui

/**
 * Doesn't work for overlay and post overlay
 */
object Resolution {

    var enabled = false
        private set

    val InitialWidthDouble = Display.getDesktopDisplayMode().width.toDouble()

    val InitialHeightDouble = Display.getDesktopDisplayMode().height.toDouble()

    var DevWidthDouble = 0.0
        private set

    var DevHeightDouble = 0.0
        private set

    var ScaleFactor = 0.0
        private set

    fun initialize(devWidth: Int = 1920, devHeight: Int = 1280) {
        DevWidthDouble = devWidth.toDouble()
        DevHeightDouble = devHeight.toDouble()
        enabled = true
        updateResolution()
    }

    internal fun updateResolution() {
        if (!enabled) {
            return
        }
        val api = UIEngine.clientApi
        val mc = api.minecraft()
        val clientResolution = UIEngine.clientApi.resolution()

        val scale = clientResolution.scaleFactor
        val widthScale = mc.displayWidth / InitialWidthDouble
        val heightScale = mc.displayHeight / InitialHeightDouble

        val devAndPlayerProportion =
            (InitialHeightDouble + InitialWidthDouble) / (DevWidthDouble + DevHeightDouble)
        ScaleFactor =
            ((widthScale + heightScale) / (2.0 * scale)) * devAndPlayerProportion
    }
}

fun ContextGui.updateResolution() {
    if (!Resolution.enabled) {
        return
    }

    val root = children.firstOrNull() ?: return
    root.origin = CENTER
    root.align = CENTER

    val scale = Resolution.ScaleFactor
    root.scale = V3(scale, scale, 1.0)
}