package com.xcue.xcuelib.configuration;


import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class ConfigSource {
    public abstract <T> T getObject(@NonNull String path, @NonNull Class<T> clazz);

    public abstract <T> T getObject(@NonNull String path, @NonNull Class<T> clazz, T def);

    public abstract void reload();

    public abstract boolean isSet(@NonNull String path);
}
