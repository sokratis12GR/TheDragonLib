/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package net.thedragonteam.thedragonlib.util;

import java.util.Calendar;

public class HolidayUtils {

    public boolean isChristmas() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) > 22 && calendar.get(Calendar.DAY_OF_MONTH) > 29;
    }
}
