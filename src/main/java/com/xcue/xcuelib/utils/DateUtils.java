package com.xcue.xcuelib.utils;

public final class DateUtils {
    /**
     *
     * @param ticks Amount of tikcs
     * @return Formatted string that goes to days at most
     */
    // Can update this to include more than just days... weeks/months
    public static String formatTicks(long ticks) {
        long days = ticks / 20 / 60 / 60 / 24;
        long hours = ticks / 20 / 60 / 60 % 24;
        long minutes = ticks / 20 / 60 % 60;
        long seconds = ticks / 20 % 60;

        String time = "";
        if (days > 0) {
            time += days + "d ";
            time += String.format("%02dh ", hours);
            time += String.format("%02dm ", minutes);
        } else if (hours > 0) {
            time += hours + "h ";
            time += String.format("%02dm ", minutes);
        } else if (minutes > 0) {
            time += minutes + "m ";
        }
        time += String.format("%02ds", seconds);

        return time;
    }
}
