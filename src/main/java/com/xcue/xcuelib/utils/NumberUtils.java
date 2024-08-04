package com.xcue.xcuelib.utils;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NumberUtils {

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isBigInt(String s) {
        try {
            BigInteger.valueOf(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static int randomInt(int max) {
        return ThreadLocalRandom.current().nextInt(max + 1);
    }

    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

    public static double randomDouble(double max) {
        return ThreadLocalRandom.current().nextDouble(max + 1);
    }

    public static String convertCooldown(long time) {
        long seconds = time / 1000L;
        long minutes = 0L;
        while (seconds > 60L) {
            seconds -= 60L;
            minutes += 1L;
        }
        long hours = 0L;
        while (minutes > 60L) {
            minutes -= 60L;
            hours += 1L;
        }
        String sHour = "";
        String sMinute = "";
        String sSecond = "";
        if (hours > 0) {
            sHour = "" + hours + " hour(s) ";
        }
        if (minutes > 0) {
            sMinute = "" + minutes + " minutes(s) ";
        }
        if (seconds > 0) {
            sSecond = "" + seconds + " second(s)";
        }
        String message = sHour + sMinute + sSecond;
        if (message.isEmpty()) {
            return "now";
        }
        return message;
    }

    @Nonnull
    public static Color parseColor(@Nullable String str) {
        if (Strings.isNullOrEmpty(str))
            return Color.BLACK;
        String[] rgb = StringUtils.split(StringUtils.deleteWhitespace(str), ',');
        if (rgb.length < 3)
            return Color.WHITE;
        return Color.fromRGB(org.apache.commons.lang.math.NumberUtils.toInt(rgb[0], 0), org.apache.commons.lang.math.NumberUtils.toInt(rgb[1], 0), org.apache.commons.lang.math.NumberUtils.toInt(rgb[2], 0));
    }

    public static DecimalFormat decimalFormat(String format) {
        return new DecimalFormat(format);
    }

    public static int getPercentage(int max, double percentage) {
        return (int) (max * (percentage / 100));
    }

    public String getRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder string = new StringBuilder();
        Random random = new Random();

        while (string.length() < length) {
            int index = (int) (random.nextFloat() * characters.length());
            string.append(characters.charAt(index));
        }

        return string.toString();
    }
}
