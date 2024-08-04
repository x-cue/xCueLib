package com.xcue.xcuelib.utils;

import com.xcue.xcuelib.compatibility.XSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class SoundUtils {

    public static void playSound(Player player, String soundStr) {
        String[] sound = soundStr.split(";");
        player.playSound(player.getLocation(), XSound.matchXSound(sound[0]).get().parseSound(), Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
    }

    public static void playSound(Player[] players, String soundStr) {
        String[] sound = soundStr.split(";");
        Arrays.stream(players).forEach(p -> p.playSound(p.getLocation(), XSound.matchXSound(sound[0]).get().parseSound(), Float.parseFloat(sound[1]), Float.parseFloat(sound[2])));
    }

    public static void playSound(Player player, String sound, int delay, Plugin plugin) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> playSound(player, sound), delay);
    }
}
