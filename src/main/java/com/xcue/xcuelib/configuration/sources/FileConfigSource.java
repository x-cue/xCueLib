package com.xcue.xcuelib.configuration.sources;

import com.xcue.xcuelib.configuration.ConfigSource;
import com.xcue.xcuelib.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class FileConfigSource extends ConfigSource {
    private FileConfiguration source;
    private final Plugin plugin;
    private final String fileName;

    public FileConfigSource(@NonNull String fileName, @NonNull Plugin plugin) {
        this.fileName = fileName;
        this.plugin = plugin;
    }

    @Override
    public <T> T getObject(@NonNull String path, @NonNull Class<T> clazz) {
        return source.getObject(path, clazz);
    }

    @Override
    public <T> T getObject(@NonNull String path, @NonNull Class<T> clazz, T def) {
        return source.getObject(path, clazz, def);
    }

    @Override
    public void reload() {
        this.source = YamlConfiguration.loadConfiguration(Objects.requireNonNull(FileUtils.load(fileName, plugin)));
    }

    @Override
    public boolean isSet(@NonNull String path) {
        return source.isSet(path);
    }
}
