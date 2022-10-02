package ru.cristalix.uiengine.utility

import org.lwjgl.opengl.Display
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.overlayContext
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.Context2D
import ru.cristalix.uiengine.element.ContextGui

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

    /**
     * that's need for preserving the proportions of existing code, just set the value to 1.5
     */
    var McScaleDouble = 1.0
        private set

    fun initialize(devWidth: Int = 1920, devHeight: Int = 1280, mcScale: Double = 1.0) {
        DevWidthDouble = devWidth.toDouble()
        DevHeightDouble = devHeight.toDouble()
        McScaleDouble = mcScale
        enabled = true
        updateResolution()
    }

    internal fun updateResolution() {
        if (!enabled) return

        val api = UIEngine.clientApi
        val mc = api.minecraft()
        val clientResolution = UIEngine.clientApi.resolution()

        val scale = clientResolution.scaleFactor
        val widthScale = mc.displayWidth / InitialWidthDouble
        val heightScale = mc.displayHeight / InitialHeightDouble

        val devAndPlayerProportion =
            (InitialHeightDouble + InitialWidthDouble) / (DevWidthDouble + DevHeightDouble)
        ScaleFactor =
            ((widthScale + heightScale) / (2.0 * scale)) * McScaleDouble /* devAndPlayerProportion*/

        overlayContext.updateResolution()
//        postOverlayContext.updateResolution()
    }

    fun resolutionScale(lastParent: AbstractElement?): Double {
        var resolutionScale = 1.0
        lastParent?.let {
            if (enabled && (it is ContextGui || it == overlayContext)) resolutionScale = ScaleFactor
        }
        return resolutionScale
    }
}

fun Context2D.updateResolution() {
    if (!Resolution.enabled) return
    for (child in children) {
        child.markDirty(scaleMatrix)
        child.markDirty(offsetMatrix)
    }
}