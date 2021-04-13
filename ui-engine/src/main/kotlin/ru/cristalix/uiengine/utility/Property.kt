package ru.cristalix.uiengine.utility

// Positive numbers are matrices that are directly converted to GL transformations
const val alignMatrix = 0
const val offsetMatrix = 1
const val scaleMatrix = 2
const val rotationMatrix = 3
const val originMatrix = 4

// Amount of direct GL matrices for preallocations
const val matrixFields = 5
const val sizeMatrix = -1;
const val colorMatrix = -2;
const val uvMatrix = -3;

val allMatrices =
    intArrayOf(alignMatrix, rotationMatrix, offsetMatrix, scaleMatrix, originMatrix, sizeMatrix, colorMatrix, uvMatrix)

const val X = 0
const val Y = 1
const val Z = 2
const val Degrees = 3
const val Red = 0
const val Green = 1
const val Blue = 2
const val Alpha = 3



operator fun DoubleArray.get(property: Property): Double {
    return this[property.ordinal]
}

operator fun DoubleArray.set(property: Property, value: Number): Double {
    val doubleValue = value.toDouble()
    this[property.ordinal] = doubleValue
    return doubleValue
}

enum class Property(
    vararg val matrixInfluence: Int
) {

    OffsetX(offsetMatrix),
    OffsetY(offsetMatrix),
    OffsetZ(offsetMatrix),

    ScaleX(scaleMatrix),
    ScaleY(scaleMatrix),
    ScaleZ(scaleMatrix),

    AlignX(alignMatrix),
    AlignY(alignMatrix),
    AlignZ(alignMatrix),

    OriginX(originMatrix),
    OriginY(originMatrix),
    OriginZ(originMatrix),

    RotationX(rotationMatrix),
    RotationY(rotationMatrix),
    RotationZ(rotationMatrix),
    RotationAngle(rotationMatrix),

    ColorA(colorMatrix),
    ColorR(colorMatrix),
    ColorG(colorMatrix),
    ColorB(colorMatrix),

    ParentSizeX(alignMatrix),
    ParentSizeY(alignMatrix),
    ParentSizeZ(alignMatrix),

    SizeX(sizeMatrix, originMatrix),
    SizeY(sizeMatrix, originMatrix),
    SizeZ(sizeMatrix, originMatrix),

    TextureX(uvMatrix),
    TextureY(uvMatrix),
    TextureWidth(uvMatrix),
    TextureHeight(uvMatrix),

    ;

    companion object {
        val VALUES = values()
    }

}


