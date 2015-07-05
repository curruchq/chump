package com.conversant.chump.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jhill on 5/07/15.
 */
public class DateUtil {

    /**
     * Zero the time of the given date
     *
     * @param date
     * @return
     */
    public static Date zeroTime(final Date date) {
        return setTime(date, 0, 0, 0, 0);
    }

    /**
     * Set the time of the given date
     *
     * @param date
     * @param hourOfDay
     * @param minute
     * @param second
     * @param ms
     * @return new instance of java.util.Date with the time set
     */
    public static Date setTime(final Date date, final int hourOfDay, final int minute, final int second, final int ms) {
        final Calendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, hourOfDay);
        gc.set(Calendar.MINUTE, minute);
        gc.set(Calendar.SECOND, second);
        gc.set(Calendar.MILLISECOND, ms);
        return gc.getTime();
    }
}
