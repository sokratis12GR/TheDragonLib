package net.thedragonteam.thedragonlib.util

import net.thedragonteam.thedragonlib.TheDragonLib
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.util.*

object LogHelper {

    var comment = arrayOf("Sorry I did not mean to do that... Please forgive me?", "KABOOM!!!! It Blew Up!!!!", "Oh Sh** what was it this time!?!?", "WHAT DID YOU DO!?!?!?!.. Oh never mind that was me...", "HA! You thought you were going to play minecraft today? NO! You get to play \"Decode the Crash Report\" ")
    private val logger = LogManager.getLogger(TheDragonLib.MODID)

    /**
     * Log with a supplied level.
     */
    fun log(logLevel: Level, `object`: Any) {
        logger.log(logLevel, `object`.toString())
    }

    //Standard log entries.

    fun log(logLevel: Level, `object`: Any, throwable: Throwable) {
        logger.log(logLevel, `object`.toString(), throwable)
    }

    fun all(`object`: Any) {
        log(Level.ALL, `object`)
    }

    fun debug(`object`: Any) {
        log(Level.DEBUG, `object`)
    }

    fun error(`object`: Any) {
        log(Level.ERROR, `object`)
    }

    fun fatal(`object`: Any) {
        log(Level.FATAL, `object`)
    }

    fun info(`object`: Any) {
        log(Level.INFO, `object`)
    }

    fun off(`object`: Any) {
        log(Level.OFF, `object`)
    }

    fun trace(`object`: Any) {
        log(Level.TRACE, `object`)
    }

    //log with format.

    fun warn(`object`: Any) {
        log(Level.WARN, `object`)
    }

    fun all(`object`: String, vararg format: Any) {
        log(Level.ALL, String.format(`object`, *format))
    }

    fun debug(`object`: String, vararg format: Any) {
        log(Level.DEBUG, String.format(`object`, *format))
    }

    fun error(`object`: String, vararg format: Any) {
        log(Level.ERROR, String.format(`object`, *format))
    }

    fun fatal(`object`: String, vararg format: Any) {
        log(Level.FATAL, String.format(`object`, *format))
    }

    fun info(`object`: String, vararg format: Any) {
        log(Level.INFO, String.format(`object`, *format))
    }

    fun off(`object`: String, vararg format: Any) {
        log(Level.OFF, String.format(`object`, *format))
    }

    fun trace(`object`: String, vararg format: Any) {
        log(Level.TRACE, String.format(`object`, *format))
    }

    //Log Throwable with format.

    fun warn(`object`: String, vararg format: Any) {
        log(Level.WARN, String.format(`object`, *format))
    }

    fun allError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.ALL, String.format(`object`, *format), throwable)
    }

    fun debugError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.DEBUG, String.format(`object`, *format), throwable)
    }

    fun errorError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.ERROR, String.format(`object`, *format), throwable)
    }

    fun fatalError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.FATAL, String.format(`object`, *format), throwable)
    }

    fun infoError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.INFO, String.format(`object`, *format), throwable)
    }

    fun offError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.OFF, String.format(`object`, *format), throwable)
    }

    fun traceError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.TRACE, String.format(`object`, *format), throwable)
    }

    fun warnError(`object`: String, throwable: Throwable, vararg format: Any) {
        log(Level.WARN, String.format(`object`, *format), throwable)
    }

    //Log throwable.
    fun allError(`object`: String, throwable: Throwable) {
        log(Level.ALL, `object`, throwable)
    }

    fun debugError(`object`: String, throwable: Throwable) {
        log(Level.DEBUG, `object`, throwable)
    }

    fun errorError(`object`: String, throwable: Throwable) {
        log(Level.ERROR, `object`, throwable)
    }

    fun fatalError(`object`: String, throwable: Throwable) {
        log(Level.FATAL, `object`, throwable)
    }

    fun infoError(`object`: String, throwable: Throwable) {
        log(Level.INFO, `object`, throwable)
    }

    fun offError(`object`: String, throwable: Throwable) {
        log(Level.OFF, `object`, throwable)
    }

    fun traceError(`object`: String, throwable: Throwable) {
        log(Level.TRACE, `object`, throwable)
    }

    fun warnError(`object`: String, throwable: Throwable) {
        log(Level.WARN, `object`, throwable)
    }

    //Log with trace element.
    fun bigAll(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        all("****************************************")
        all("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            all("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        all("****************************************")
    }

    fun bigDebug(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        debug("****************************************")
        debug("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            debug("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        debug("****************************************")
    }

    fun bigError(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        error("****************************************")
        error("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            error("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        error("****************************************")
    }

    fun bigFatal(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        fatal("****************************************")
        fatal("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            fatal("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        fatal("****************************************")
    }

    fun bigInfo(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        info("****************************************")
        info("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            info("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        info("****************************************")
    }

    fun bigOff(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        off("****************************************")
        off("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            off("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        off("****************************************")
    }

    fun bigTrace(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        trace("****************************************")
        trace("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            trace("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        trace("****************************************")
    }

    fun bigWarn(format: String, vararg data: Any) {
        val trace = Thread.currentThread().stackTrace
        warn("****************************************")
        warn("* " + format, *data)
        var i = 2
        while (i < 8 && i < trace.size) {
            warn("*  at %s%s", trace[i].toString(), if (i == 7) "..." else "")
            i++
        }
        warn("****************************************")
    }

    fun fatalErrorMessage(error: String) {
        error(comment[Random(System.nanoTime()).nextInt(comment.size)])
        error("*************************************************************************************")
        error("It looks like a fatal error occurred which has caused the game to crash... [%s]", error)
        error("Please go here for assistance: https://github.com/TheDragonTeam/TheDragonLib/issues")
        error("*************************************************************************************")
    }
}