package net.thedragonteam.thedragonlib.client.util

import net.thedragonteam.thedragonlib.util.LogHelper

import java.net.URI

object ClientUtills {

    fun openLink(url: String) {
        try {
            val uri = URI(url)
            val oclass = Class.forName("java.awt.Desktop")
            val `object` = oclass.getMethod("getDesktop", *arrayOfNulls<Class<*>>(0)).invoke(null)
            oclass.getMethod("browse", *arrayOf<Class<*>>(URI::class.java)).invoke(`object`, uri)
        } catch (throwable: Throwable) {
            LogHelper.error("Couldn\'t open link")
            throwable.printStackTrace()
        }

    }
}