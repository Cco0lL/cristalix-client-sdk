package ru.cristalix.uiengine.utility

import org.lwjgl.opengl.Display
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.Context2D
import kotlin.math.min

object Resolution {

    private val contexts = arrayListOf<Context2D>()

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

    fun initialize(devWidth: Int = 1920, devHeight: Int = 1080) {
        DevWidthDouble = devWidth.toDouble()
        DevHeightDouble = devHeight.toDouble()
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

        val windowScale = min(widthScale, heightScale)
        val devAndPlayerProportion = (InitialHeightDouble + InitialWidthDouble) / (DevWidthDouble + DevHeightDouble)
        ScaleFactor = (windowScale / scale) * devAndPlayerProportion

        for (context in contexts) {
            context.updateResolution()
        }
    }

    fun resolutionScale(parent: AbstractElement?) = parent?.let {
        if (it is Context2D && contexts.contains(it)) ScaleFactor else 1.0
    } ?: 1.0

    fun Context2D.enableAutoResolution() {
        if (!enabled) return
        contexts += this
        updateResolution()
    }

    fun Context2D.disableAutoResolution() {
        if (!enabled) return
        contexts -= this
    }

    private fun Context2D.updateResolution() {
        for (child in children) {
            child.markDirty(scaleMatrix)
            child.markDirty(offsetMatrix)
        }
    }
}