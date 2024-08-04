package com.xcue.xcuelib.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private final List<ConfigSource> sources;
    private final Map<String, ConfigValue<?>> cache;

    public Config() {
        this.sources = new ArrayList<>();
        this.cache = new HashMap<>();
    }

    public void reload() {
        cache.clear();
        // TODO: Consider automatically repopulating each value in cache upon reload
        // TODO: This can be done by looping through each value and running get(key, value); after each source is
        //  reloaded...
        for (ConfigSource source : sources) {
            source.reload();
        }
    }

    public void addSource(ConfigSource source) {
        this.sources.add(source);
    }

    @Nullable
    public <T> T get(@NonNull String path, @NonNull Class<T> clazz) {
        for (ConfigSource source : sources) {
            if (source.isSet(path)) {
                return source.getObject(path, clazz);
            }
        }

        return null;
    }

    public <T> T get(@NonNull String path, Class<T> clazz, T def) {
        if (cache.containsKey(path)) {
            return ((ConfigValue<T>)cache.get(path)).get();
        }

        T value = get(path, clazz);

        if (value == null) {
            value = def;
        }

        cache.put(path, new ConfigValue<>(value));
        return value;
    }
}
