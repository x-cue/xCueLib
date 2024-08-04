package com.xcue.xcuelib.utils;

import org.bukkit.ChatColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextUtils {

    /**
     *
     * @param text String to capitalize
     * @return String with the very first letter capitalized
     * @example mary poppins --> Mary poppins
     */
    @NonNull
    public static String capitalizeFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Capitalizes every word separated by a space
     * @param str String to transform
     * @return Same string but in title case
     * @example mary poppins --> Mary Poppins
     */
    @NonNull
    public static String toTitleCase(@NonNull String str) {
        return Arrays.stream(str.split(" ")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
    }

    /**
     *
     * @param str String to translate
     * @param pref Color code prefix
     * @return String with color translated by pref
     */
    @NonNull
    public static String translateColor(@NonNull String str, char pref) {
        return ChatColor.translateAlternateColorCodes(pref, str);
    }

    /**
     *
     * @param str String to translate
     * @return String with color translated using '&'
     */
    @NonNull
    public static String translateColor(@NonNull String str) {
        return translateColor(str, '&');
    }
}