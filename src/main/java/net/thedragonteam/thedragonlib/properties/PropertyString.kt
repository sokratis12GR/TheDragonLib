package net.thedragonteam.thedragonlib.properties

import com.google.common.base.Optional
import com.google.common.collect.ImmutableSet
import net.minecraft.block.properties.PropertyHelper
import net.thedragonteam.thedragonlib.util.ArrayUtils
import net.thedragonteam.thedragonlib.util.LogHelper
import java.util.*

class PropertyString(name: String, vararg values: String) : PropertyHelper<String>(name, String::class.java) {

    private val valuesSet: List<String>

    init {
        valuesSet = ArrayList<String>()
        Collections.addAll<String>(valuesSet, *ArrayUtils.arrayToLowercase(values))
    }

    override fun getAllowedValues(): Collection<String> {
        return ImmutableSet.copyOf(valuesSet)
    }

    override fun parseValue(value: String): Optional<String> {
        return if (valuesSet.contains(value)) Optional.of(value) else Optional.absent<String>()
    }

    override fun getName(value: String): String {
        return value
    }

    fun toMeta(value: String): Int {
        return if (valuesSet.contains(value)) valuesSet.indexOf(value) else 0
    }

    fun fromMeta(meta: Int): String {
        if (meta >= 0 && meta < valuesSet.size) {
            return valuesSet[meta]
        }
        LogHelper.error("[PropertyString] Attempted to load property for invalid meta value")
        return valuesSet[0]
    }
}