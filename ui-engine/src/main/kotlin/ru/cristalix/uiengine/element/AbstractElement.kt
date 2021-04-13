package ru.cristalix.uiengine.element

import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.matrixBuffer
import ru.cristalix.uiengine.utility.*
import ru.cristalix.uiengine.utility.Property.*
import java.lang.IllegalStateException


abstract class AbstractElement() {

    internal val properties: DoubleArray = DoubleArray(Property.VALUES.size)
    val matrices: Array<Matrix4f?> = arrayOfNulls(matrixFields)

    open var context: Context? = null

    private var dirtyMatrices: MutableList<Int>? = null
    internal var animationContext: AnimationContext? = null
    protected var cachedHexColor: Int = 0
    internal var hovered: Boolean = false
    internal var passedHoverCulling: Boolean = false

    var beforeRender: (() -> Unit)? = null
    var afterRender: (() -> Unit)? = null

    var enabled: Boolean = true
    var onClick: ClickHandler? = null
    var onHover: HoverHandler? = null

    var offset: V
        get() = ProxiedV(OffsetX.ordinal, this)
        set(value) = value.write(offset)

    var scale: V
        get() = ProxiedV(ScaleX.ordinal, this)
        set(value) = value.write(scale)

    var align: V
        get() = ProxiedV(AlignX.ordinal, this)
        set(value) = value.write(align)

    var origin: V
        get() = ProxiedV(OriginX.ordinal, this)
        set(value) = value.write(origin)

    var color: Color
        get() = ProxiedColor(this)
        set(value) = value.write(color)

    var rotation: Rotation
        get() = ProxiedRotation(this)
        set(value) = value.write(rotation)

    var size: V
        get() = ProxiedV(SizeX.ordinal, this)
        set(value) = value.write(size)

    var alignOrigin: V
        get() = align
        set(value) {
            align = value
            origin = value
        }

    init {
        this.scale = V(1, 1, 1)
        this.changeProperty(RotationZ.ordinal, 1.0)
    }
    
    constructor(setup: AbstractElement.() -> Unit) : this() {
        setup()
    }

    internal fun changeProperty(index: Int, value: Number) {

        val property = Property.VALUES[index]

        val animationContext = this.animationContext
        if (animationContext != null) {

            val context = context ?: throw IllegalStateException("Tried to animate an orphan element (no context)")

            var animation: Animation? = null
            for (existing in context.runningAnimations) {
                if (existing.element === this && existing.property.ordinal == index) {
                    animation = existing
                    break
                }
            }
            if (animation == null) animation = Animation(this, property)

            animation.newTarget(
                value.toDouble(),
                (animationContext.duration.toDouble() * 1000).toLong(),
                animationContext.easing
            )
            context.runningAnimations.add(animation)

            return
        }
        // stdout.println('setting ' + propertyId + ' to ' + value);
        this.properties[index] = value.toDouble()
        for (matrix in property.matrixInfluence) {
            this.markDirty(matrix)
        }

    }

    fun cleanMatrices() {
        val dirty = this.dirtyMatrices ?: return
        for (matrix in dirty.toIntArray()) {
            this.updateMatrix(matrix)
        }
        this.dirtyMatrices = null
    }

    private fun vec(x: Number, y: Number, z: Number): Vector3f {
        return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    }

    open fun updateMatrix(matrixId: Int) {
        val properties = this.properties
        dirtyMatrices?.remove(matrixId)


        if (matrixId == colorMatrix) {
            this.cachedHexColor = this.color.toGuiHex()
        }

        if (matrixId >= 0) {
            val matrix: Matrix4f = Matrix4f().setIdentity() as Matrix4f

            when (matrixId) {
                alignMatrix -> matrix.translate(
                    vec(
                        properties[AlignX] * properties[ParentSizeX],
                        properties[AlignY] * properties[ParentSizeY],
                        properties[AlignZ] * properties[ParentSizeZ]
                    )
                )
                rotationMatrix -> matrix.rotate(
                    properties[RotationAngle].toFloat(), vec(
                        properties[RotationX],
                        properties[RotationY],
                        properties[RotationZ]
                    )
                )
                offsetMatrix -> matrix.translate(
                    vec(
                        properties[OffsetX],
                        properties[OffsetY],
                        properties[OffsetZ]
                    )
                )
                scaleMatrix -> matrix.scale(
                    vec(
                        properties[ScaleX],
                        properties[ScaleY],
                        properties[ScaleZ]
                    )
                )
                originMatrix -> matrix.translate(
                    vec(
                        -properties[OriginX] * properties[SizeX],
                        -properties[OriginY] * properties[SizeY],
                        -properties[OriginZ] * properties[SizeZ]
                    )
                )
            }
            this.matrices[matrixId] = matrix
        }
    }

    fun markDirty(matrix: Int) {
        val matrices = dirtyMatrices ?: ArrayList()
        if (!matrices.contains(matrix)) matrices.add(matrix)
        dirtyMatrices = matrices

        if (matrix == sizeMatrix) {
            markDirty(originMatrix)
        }
    }

    open fun transformAndRender() {
        if (!this.enabled) return;

        GlStateManager.pushMatrix()
        this.applyTransformations()

//        if (this.beforeRender) this.beforeRender()

        this.beforeRender?.invoke()
        this.render()
        this.afterRender?.invoke()

//        if (this.afterRender) this.afterRender()

        GlStateManager.popMatrix()
    }

    open fun applyTransformations() {

        if (!this.enabled) return;

        this.cleanMatrices();

        GlStateManager.color(
            properties[ColorR].toFloat(),
            properties[ColorG].toFloat(),
            properties[ColorB].toFloat(),
            properties[ColorA].toFloat(),
        )

        for (matrix in this.matrices) {
            if (matrix == null) continue
            matrix.store(matrixBuffer)
            matrixBuffer.flip()
            GlStateManager.multMatrix(matrixBuffer)
        }
    }

//    fun update(action: Element.() -> Unit): Element {
//        TODO("Not yet implemented")
//        return getThis()
//    }
//
//    infix fun then(action: () -> Unit): Element {
//        TODO("Not yet implemented")
//        return getThis()
//    }
//
//    fun then(delay: Number, action: () -> Unit): Element {
//        TODO("Not yet implemented")
//        return getThis()
//    }

    abstract fun render()


}
