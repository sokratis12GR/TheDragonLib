package net.thedragonteam.thedragonlib.lib

import net.minecraft.util.math.BlockPos

class Vec3I {

    var x: Int = 0
    var y: Int = 0
    var z: Int = 0

    constructor() {
    }

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(vec3I: Vec3I) {
        this.x = vec3I.x
        this.y = vec3I.y
        this.z = vec3I.z
    }

    constructor(pos: BlockPos) {
        this.x = pos.x
        this.y = pos.y
        this.z = pos.z
    }

    operator fun set(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun set(vec: Vec3I) {
        this.x = vec.x
        this.y = vec.y
        this.z = vec.z
    }

    fun set(pos: BlockPos) {
        this.x = pos.x
        this.y = pos.y
        this.z = pos.z
    }

    fun add(x: Int, y: Int, z: Int) {
        this.x += x
        this.y += y
        this.z += z
    }

    fun add(vec: Vec3I) {
        this.x += vec.x
        this.y += vec.y
        this.z += vec.z
    }

    fun add(pos: BlockPos) {
        this.x += pos.x
        this.y += pos.y
        this.z += pos.z
    }

    fun subtract(pos: BlockPos) {
        this.x -= pos.x
        this.y -= pos.y
        this.z -= pos.z
    }

    fun subtract(vec: Vec3I) {
        this.x -= vec.x
        this.y -= vec.y
        this.z -= vec.z
    }

    fun subtract(x: Int, y: Int, z: Int) {
        this.x -= x
        this.y -= y
        this.z -= z
    }

    fun copy(): Vec3I {
        return Vec3I(this)
    }

    val pos: BlockPos
        get() = BlockPos(x, y, z)

    override fun toString(): String {
        return String.format("Vec3I: [x: %s, y: %s, z: %s]", x, y, z)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null || javaClass != obj.javaClass) return false
        val other = obj as Vec3I?

        return x == other!!.x && y == other.y && z == other.z

    }

    override fun hashCode(): Int {
        return (y + z * 31) * 31 + x
    }
}