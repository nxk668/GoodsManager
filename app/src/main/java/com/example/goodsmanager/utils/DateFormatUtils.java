package com.example.goodsmanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateFormatUtils {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

    private DateFormatUtils() {
    }

    public static String format(Date date) {
        if (date == null) {
            return "--";
        }
        return new SimpleDateFormat(DEFAULT_PATTERN, Locale.CHINA).format(date);
    }

    public static Date parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat(DEFAULT_PATTERN, Locale.CHINA).parse(text);
        } catch (ParseException e) {
            return null;
        }
    }
}

