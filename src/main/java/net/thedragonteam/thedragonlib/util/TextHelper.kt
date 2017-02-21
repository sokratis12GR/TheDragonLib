package net.thedragonteam.thedragonlib.util

//General place to call text formatting from. You can alternatively use 'EnumChatFormatting'.


import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.commons.lang3.text.WordUtils
import java.util.*

@SideOnly(Side.CLIENT)
object TextHelper {
    fun getFormattedText(string: String): String {
        return string.replace("&".toRegex(), "\u00A7")
    }

    fun localize(input: String, vararg format: Any): String {
        return I18n.format(input, *format)
    }

    fun localizeEffect(input: String, vararg format: Any): String {
        return getFormattedText(localize(input, *format))
    }

    fun localizeAll(input: Array<String>): Array<String?> {
        val ret = arrayOfNulls<String>(input.size)
        for (i in input.indices)
            ret[i] = localize(input[i])

        return ret
    }

    fun localizeAllEffect(input: Array<String>): Array<String?> {
        val ret = arrayOfNulls<String>(input.size)
        input.indices.forEach { i -> ret[i] = localizeEffect(input[i]) }

        return ret
    }

    fun localizeAll(input: List<String>): ArrayList<String> {
        val ret = ArrayList<String>(input.size)
        input.indices.forEach { i -> ret.add(i, localize(input[i])) }

        return ret
    }

    fun localizeAllEffect(input: List<String>): ArrayList<String> {
        val ret = ArrayList<String>(input.size)
        input.indices.forEach { i -> ret.add(i, localizeEffect(input[i])) }

        return ret
    }

    @JvmOverloads fun cutLongString(string: String, characters: Int = 30): Array<String> {
        return WordUtils.wrap(string, characters, "/cut", false).split("/cut".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }
}