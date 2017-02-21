package net.thedragonteam.thedragonlib.lib

import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.thedragonteam.thedragonlib.util.Utils

class Vec3D {

    var x: Double = 0.toDouble()
    var y: Double = 0.toDouble()
    var z: Double = 0.toDouble()

    constructor() {
    }

    constructor(entity: Entity) {
        this.x = entity.posX
        this.y = entity.posY
        this.z = entity.posZ
    }

    constructor(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(vec3I: Vec3D) {
        this.x = vec3I.x
        this.y = vec3I.y
        this.z = vec3I.z
    }

    constructor(pos: BlockPos) {
        this.x = pos.x.toDouble()
        this.y = pos.y.toDouble()
        this.z = pos.z.toDouble()
    }

    operator fun set(x: Double, y: Double, z: Double): Vec3D {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(vec: Vec3D): Vec3D {
        this.x = vec.x
        this.y = vec.y
        this.z = vec.z
        return this
    }

    fun set(pos: BlockPos): Vec3D {
        this.x = pos.x.toDouble()
        this.y = pos.y.toDouble()
        this.z = pos.z.toDouble()
        return this
    }

    fun add(x: Double, y: Double, z: Double): Vec3D {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    fun add(vec: Vec3D): Vec3D {
        this.x += vec.x
        this.y += vec.y
        this.z += vec.z
        return this
    }

    fun add(pos: BlockPos): Vec3D {
        this.x += pos.x.toDouble()
        this.y += pos.y.toDouble()
        this.z += pos.z.toDouble()
        return this
    }

    fun subtract(pos: BlockPos): Vec3D {
        this.x -= pos.x.toDouble()
        this.y -= pos.y.toDouble()
        this.z -= pos.z.toDouble()
        return this
    }

    fun subtract(vec: Vec3D): Vec3D {
        this.x -= vec.x
        this.y -= vec.y
        this.z -= vec.z
        return this
    }

    fun subtract(x: Double, y: Double, z: Double): Vec3D {
        this.x -= x
        this.y -= y
        this.z -= z
        return this
    }

    fun multiply(vec: Vec3D): Vec3D {
        this.x *= vec.x
        this.y *= vec.y
        this.z *= vec.z
        return this
    }

    fun multiply(x: Double, y: Double, z: Double): Vec3D {
        this.x *= x
        this.y *= y
        this.z *= z
        return this
    }

    fun copy(): Vec3D {
        return Vec3D(this)
    }

    val pos: BlockPos
        get() = BlockPos(x, y, z)

    override fun toString(): String {
        return String.format("Vec3D: [x: %s, y: %s, z: %s]", x, y, z)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null || javaClass != obj.javaClass) return false
        val other = obj as Vec3D?

        return x == other!!.x && y == other.y && z == other.z

    }

    override fun hashCode(): Int {
        return (y.toInt() + z.toInt() * 31) * 31 + x.toInt()
    }

    companion object {

        /**
         * Calculates a directional vector between the two given points
         * This can be used for example if you have an entity at pos1 and you want to
         * apply motion so hat is moves towards pos2
         */
        fun getDirectionVec(vecFrom: Vec3D, vecTo: Vec3D): Vec3D {
            var distance = Utils.getDistanceAtoB(vecFrom, vecTo)
            if (distance == 0.0) {
                distance = 0.1
            }
            val offset = vecTo.copy()
            offset.subtract(vecFrom)
            return Vec3D(offset.x / distance, offset.y / distance, offset.z / distance)
        }
    }
}