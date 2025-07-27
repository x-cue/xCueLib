package com.xcue.xcuelib.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class Config {
    private final Map<String, ConfigSource> sources;
    private final Map<String, ConfigValue<?>> cache;

    public Config() {
        this.sources = new HashMap<>();
        this.cache = new HashMap<>();
    }

    public void reload() {
        cache.clear();
        // TODO: Consider automatically repopulating each value in cache upon reload
        // TODO: This can be done by looping through each value and running get(key, value); after each source is
        //  reloaded...
        for (ConfigSource source : sources()) {
            source.reload();
        }
    }

    private Collection<ConfigSource> sources() {
        return this.sources.values();
    }

    public void addSource(String name, ConfigSource source) {
        this.sources.put(name, source);
    }

    public ConfigSource removeSource(String name) {
        return this.sources.remove(name);
    }

    @Nullable
    public <T> T get(@NonNull String path, @NonNull Class<T> clazz) {
        for (ConfigSource source : sources()) {
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
