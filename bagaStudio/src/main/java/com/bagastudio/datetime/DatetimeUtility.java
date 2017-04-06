package com.bagastudio.datetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatetimeUtility
{
    public static String dateToday()
    {
        final String nowDate;
        final Calendar calendar = Calendar.getInstance();
        System.out.println("Current time => " + calendar.getTime());

        final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        nowDate = df.format(calendar.getTime());
        return nowDate;
    }

}
