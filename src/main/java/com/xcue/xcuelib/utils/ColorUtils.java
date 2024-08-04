package com.xcue.xcuelib.utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Map<String, Color> COLOR_NAMES = new HashMap<>();
    private static final Pattern CHAT_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-OR]");

    static {
        COLOR_NAMES.put("WHITE", Color.WHITE);
        COLOR_NAMES.put("SILVER", Color.SILVER);
        COLOR_NAMES.put("GRAY", Color.GRAY);
        COLOR_NAMES.put("BLACK", Color.BLACK);
        COLOR_NAMES.put("RED", Color.RED);
        COLOR_NAMES.put("MAROON", Color.MAROON);
        COLOR_NAMES.put("YELLOW", Color.YELLOW);
        COLOR_NAMES.put("OLIVE", Color.OLIVE);
        COLOR_NAMES.put("LIME", Color.LIME);
        COLOR_NAMES.put("GREEN", Color.GREEN);
        COLOR_NAMES.put("AQUA", Color.AQUA);
        COLOR_NAMES.put("TEAL", Color.TEAL);
        COLOR_NAMES.put("BLUE", Color.BLUE);
        COLOR_NAMES.put("NAVY", Color.NAVY);
        COLOR_NAMES.put("FUCHSIA", Color.FUCHSIA);
        COLOR_NAMES.put("PURPLE", Color.PURPLE);
        COLOR_NAMES.put("ORANGE", Color.ORANGE);
    }

    @Nullable
    public static Color getColor(String paramString) {
        if (StringUtils.isEmpty(paramString))
            return null;
        Color color = parseColorName(paramString);
        if (color == null) {
            String[] arrayOfString = paramString.split(",");
            if (arrayOfString.length == 3)
                try {
                    color = Color.fromRGB(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]),
                            Integer.parseInt(arrayOfString[2]));
                } catch (NumberFormatException numberFormatException) {}
        }
        return color;
    }

    @Nullable
    private static Color parseColorName(String paramString) {
        return COLOR_NAMES.get(paramString);
    }

    public static DyeColor getDyeColor(String paramString) {
        DyeColor dyeColor = null;
        try {
            dyeColor = DyeColor.valueOf(paramString.toUpperCase());
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        }
        return dyeColor;
    }

    public static Color getFireworkColor(String paramString) {
        return getDyeColor(paramString).getFireworkColor();
    }

    public static String removeChatColors(String input) {
        return CHAT_COLOR_PATTERN.matcher(input).replaceAll("");
    }
}
