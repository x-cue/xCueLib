package com.xcue.xcuelib.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.UUID;

public class LocationUtils {
    /**
     * Serializes a location of a block
     *
     * @param loc Location to serialize
     * @param asBlock Whether cords shown as Float or Int
     * @return Serialized location as a String
     */
    @NonNull
    public static String serializeLocation(@NonNull Location loc, boolean asBlock) {
        return String.format("%s %f/%f/%f %f/%f", Objects.requireNonNull(loc.getWorld()).getUID(),
                asBlock ? loc.getBlockX() : loc.getX(),
                asBlock ? loc.getBlockY() : loc.getY(),
                asBlock ? loc.getBlockZ() : loc.getZ(),
                loc.getYaw(),
                loc.getPitch()
                ).trim();
    }

    /**
     * Deserializes a location of a block
     *
     * @param location String to deserialize
     * @param asBlock Whether cords shown as Float or Int
     * @return Deserialized String as a Location
     */
    @NonNull
    public static Location deserializeLocation(@NonNull String location, boolean asBlock) {
        String[] parts = location.split(" ");
        String[] cords = parts[1].split("/");
        String[] pitchYaw = parts[2].split("/");

        return new Location(Bukkit.getWorld(UUID.fromString(parts[0])),
                asBlock ? Integer.parseInt(cords[0]) : Double.parseDouble(cords[0]),
                asBlock ? Integer.parseInt(cords[1]) : Double.parseDouble(cords[1]),
                asBlock ? Integer.parseInt(cords[2]) : Double.parseDouble(cords[2]),
                Float.parseFloat(pitchYaw[0]),
                Float.parseFloat(pitchYaw[1])
        );
    }

    /**
     *
     * @param loc Location to serialize (as non-block)
     * @return Serialized location
     */
    public static String serializeLocation(@NonNull Location loc) {
        return serializeLocation(loc, false);
    }

    /**
     *
     * @param location Location to deserialize
     * @return Deserialized location (as non-block)
     */
    public static Location deserializeLocation(@NonNull String location) {
        return deserializeLocation(location, false);
    }

    public static boolean areSameBlock(Location a, Location b) {
        if (a == null || b == null) return false;
        if (!a.getWorld().getName().equals(b.getWorld().getName())) return false;
        return a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
