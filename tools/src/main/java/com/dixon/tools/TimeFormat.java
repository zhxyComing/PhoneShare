package com.dixon.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormat {

    public static String longToString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        return format.format(new Date(time * 1000));
    }
}
