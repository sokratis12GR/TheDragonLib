package com.sofodev.thedragonlib.util;

import com.sofodev.thedragonlib.TheDragonLib;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class LogHelper {

    public static String[] comment = {"Sorry I did not mean to do that... Please forgive me?", "KABOOM!!!! It Blew Up!!!!", "Oh Sh** what was it this time!?!?", "WHAT DID YOU DO!?!?!?!.. Oh never mind that was me...", "HA! You thought you were going to play minecraft today? NO! You get to play \"Decode the Crash Report\" "};
    private static Logger logger = LogManager.getLogger(TheDragonLib.MODID);

    public static Logger getLogger() {
        return LogManager.getLogger(TheDragonLib.MODID);
    }

    public static Logger getLogger(String modid) {
        return LogManager.getLogger(modid);
    }

    /**
     * Log with a supplied level.
     */
    public static void log(Level logLevel, Object object) {
        logger.log(logLevel, String.valueOf(object));
    }

    //Standard log entries.

    public static void log(Level logLevel, Object object, Throwable throwable) {
        logger.log(logLevel, String.valueOf(object), throwable);
    }

    public static void all(Object object) {
        log(Level.ALL, object);
    }

    public static void debug(Object object) {
        log(Level.DEBUG, object);
    }

    public static void error(Object object) {
        log(Level.ERROR, object);
    }

    public static void fatal(Object object) {
        log(Level.FATAL, object);
    }

    public static void info(Object object) {
        log(Level.INFO, object);
    }

    public static void off(Object object) {
        log(Level.OFF, object);
    }

    public static void trace(Object object) {
        log(Level.TRACE, object);
    }

    //log with format.

    public static void warn(Object object) {
        log(Level.WARN, object);
    }

    public static void all(String object, Object... format) {
        log(Level.ALL, String.format(object, format));
    }

    public static void debug(String object, Object... format) {
        log(Level.DEBUG, String.format(object, format));
    }

    public static void error(String object, Object... format) {
        log(Level.ERROR, String.format(object, format));
    }

    public static void fatal(String object, Object... format) {
        log(Level.FATAL, String.format(object, format));
    }

    public static void info(String object, Object... format) {
        log(Level.INFO, String.format(object, format));
    }

    public static void off(String object, Object... format) {
        log(Level.OFF, String.format(object, format));
    }

    public static void trace(String object, Object... format) {
        log(Level.TRACE, String.format(object, format));
    }

    //Log Throwable with format.

    public static void warn(String object, Object... format) {
        log(Level.WARN, String.format(object, format));
    }

    public static void allError(String object, Throwable throwable, Object... format) {
        log(Level.ALL, String.format(object, format), throwable);
    }

    public static void debugError(String object, Throwable throwable, Object... format) {
        log(Level.DEBUG, String.format(object, format), throwable);
    }

    public static void errorError(String object, Throwable throwable, Object... format) {
        log(Level.ERROR, String.format(object, format), throwable);
    }

    public static void fatalError(String object, Throwable throwable, Object... format) {
        log(Level.FATAL, String.format(object, format), throwable);
    }

    public static void infoError(String object, Throwable throwable, Object... format) {
        log(Level.INFO, String.format(object, format), throwable);
    }

    public static void offError(String object, Throwable throwable, Object... format) {
        log(Level.OFF, String.format(object, format), throwable);
    }

    public static void traceError(String object, Throwable throwable, Object... format) {
        log(Level.TRACE, String.format(object, format), throwable);
    }

    public static void warnError(String object, Throwable throwable, Object... format) {
        log(Level.WARN, String.format(object, format), throwable);
    }

    //Log throwable.
    public static void allError(String object, Throwable throwable) {
        log(Level.ALL, object, throwable);
    }

    public static void debugError(String object, Throwable throwable) {
        log(Level.DEBUG, object, throwable);
    }

    public static void errorError(String object, Throwable throwable) {
        log(Level.ERROR, object, throwable);
    }

    public static void fatalError(String object, Throwable throwable) {
        log(Level.FATAL, object, throwable);
    }

    public static void infoError(String object, Throwable throwable) {
        log(Level.INFO, object, throwable);
    }

    public static void offError(String object, Throwable throwable) {
        log(Level.OFF, object, throwable);
    }

    public static void traceError(String object, Throwable throwable) {
        log(Level.TRACE, object, throwable);
    }

    public static void warnError(String object, Throwable throwable) {
        log(Level.WARN, object, throwable);
    }

    //Log with trace element.
    public static void bigAll(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        all("****************************************");
        all("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            all("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        all("****************************************");
    }

    public static void bigDebug(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        debug("****************************************");
        debug("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            debug("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        debug("****************************************");
    }

    public static void bigError(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        error("****************************************");
        error("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            error("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        error("****************************************");
    }

    public static void bigFatal(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        fatal("****************************************");
        fatal("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            fatal("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        fatal("****************************************");
    }

    public static void bigInfo(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        info("****************************************");
        info("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            info("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        info("****************************************");
    }

    public static void bigOff(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        off("****************************************");
        off("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            off("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        off("****************************************");
    }

    public static void bigTrace(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        trace("****************************************");
        trace("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            trace("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        trace("****************************************");
    }

    public static void bigWarn(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        warn("****************************************");
        warn("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            warn("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        warn("****************************************");
    }

    public static void fatalErrorMessage(String error) {
        error(comment[new Random(System.nanoTime()).nextInt(comment.length)]);
        error("*************************************************************************************");
        error("It looks like a fatal error occurred which has caused the game to crash... [%s]", error);
        error("Please go here for assistance: https://github.com/TheDragonTeam/TheDragonLib/issues");
        error("*************************************************************************************");
    }
}