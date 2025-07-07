package com.xcue.xcuelib.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterUtils {
    public static List<String> matchOnlinePlayers(@NonNull String query, boolean startsWith) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                .filter(x -> startsWith ? x.toLowerCase().startsWith(query.toLowerCase())
                        : x.toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
    }

    public static List<String> filterMatches(@NonNull String query, @NonNull List<String> values, boolean startsWith) {
        String lowerQuery = query.toLowerCase();
        return values.stream()
                .filter(value -> {
                    String lowerValue = value.toLowerCase();
                    return startsWith ? lowerValue.startsWith(lowerQuery) : lowerValue.contains(lowerQuery);
                })
                .collect(Collectors.toList());
    }

    public static <T extends Enum<T>> List<String> enumValues(Enum<T> enuhm) {
        return Arrays.stream(enuhm.getClass().getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }

    public static List<String> getRange(int min, int max) {
        List<String> range = new ArrayList<>();

        for (int i = min; i < max; i++) {
            range.add(String.valueOf(i));
        }

        return range;
    }

    public static List<String> getRange(int max) {
        List<String> range = new ArrayList<>();

        for (int i = 0; i < max; i++) {
            range.add(String.valueOf(i));
        }

        return range;
    }
}
