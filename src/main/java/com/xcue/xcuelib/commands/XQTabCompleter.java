package com.xcue.xcuelib.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated(forRemoval = true, since = "06/07/2025")
public abstract class XQTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command,
                                      @NonNull String s, @NonNull String[] strings) {
        return hasPermission(commandSender) ? getResults(commandSender, command, s, strings) : null;
    }

    public abstract boolean hasPermission(CommandSender sender);

    public abstract List<String> getResults(@NonNull CommandSender sender, @NonNull Command cmd,
                                            @NonNull String label, @NonNull String[] args);

    public List<String> matchOnlinePlayers(@NonNull String query, boolean startsWith) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                .filter(x -> startsWith ? x.toLowerCase().startsWith(query.toLowerCase())
                        : x.toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
    }

    public <T extends Enum<T>> List<String> enumValues(Enum<T> enuhm) {
        return Arrays.stream(enuhm.getClass().getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }

    public List<String> getRange(int min, int max) {
        List<String> range = new ArrayList<>();

        for (int i = min; i < max; i++) {
            range.add(String.valueOf(i));
        }

        return range;
    }

    public List<String> getRange(int max) {
        List<String> range = new ArrayList<>();

        for (int i = 0; i < max; i++) {
            range.add(String.valueOf(i));
        }

        return range;
    }
}
